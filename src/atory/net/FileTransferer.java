package atory.net;


import java.net.*;
import java.io.*;
import java.util.*;
import atory.*;
import atory.fs.*;
import atory.gui.*;
/**
 * Clase encargada de tratar las conexiones al puerto de datos.
 */
public class FileTransferer extends Thread
{
	ServerSocket server = null;
	static final int BUFFSIZE = 4096;
	static Vector paths = new Vector(10,5);
	static Vector ips = new Vector(10,5);
	static Vector sizes = new Vector(10,5); 
	static boolean flag = false;
	
	public FileTransferer(ServerSocket server, String path, InetAddress ip, long size)
	{
	  super("FileTransferer");
	  this.server = server;
	  addList(path,ip,size);
	}
	
	synchronized void addList(String path, InetAddress ip, long size)
	{
		while(flag)
			try {wait();} catch(Exception e){;}
		flag = true;
		paths.add(path);
		ips.add(ip);
		sizes.add(new Long(size));
		flag = false;
		notify();
	}
	
	synchronized void removeList(int n)
	{
		while(flag)
			try {wait();} catch(Exception e){;}
		flag = true;
		paths.remove(n);
		ips.remove(n);
		sizes.remove(n);
		flag = false;
		notify();
	}
	
	public void run()
	{
		boolean b = true;
		int n = 0;
		int c = 0;
		long filesize = 0;
		String name = null;
		Socket socket = null;
		File fichero = null;
		OutputStream out = null;
		try
		{
			while(b)
			{
				b = false;
				socket = server.accept();
				InetAddress client = socket.getInetAddress();
				if((n = isMine(client)) == -1) b = true;
			}
			name = (String) paths.get(n);
		   removeList(n);
			fichero = new File(name);
			InputStream in = socket.getInputStream();
		   byte[] bin = new byte[BUFFSIZE];
			//BufferedInputStream inb = new BufferedInputStream(in);
			fichero.createNewFile();
			out = new FileOutputStream(fichero);
         //BufferedOutputStream outb= new BufferedOutputStream( out );
			while((c = in.read(bin))!=-1)
			{
				out.write(bin,0,c);
			}

			//out.flush();
         out.close();
         socket.close();
			
		}
		catch(Exception e)
		{;}
		
		if(!Storage.checkIntegrity(fichero))
		{
			MainWindow.error("El fichero "+ getName() +" est corrupto");
			fichero.delete();
		}
	}
	
	int isMine(InetAddress ip)
	{
		int n = -1;
		try
		{
			for(int i = 0; i< ips.size(); i++)
			{
				if(ip.equals(ips.get(i)))
				{
					n = i;
					break;
				}
			}
		}
		catch(Exception e)
		{}
		
		return n;
	}
}
