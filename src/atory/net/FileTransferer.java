package atory.net;


import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Clase encargada de tratar las conexiones al puerto de datos.
 */
public class FileTransferer extends Thread
{
	ServerSocket server = null;
	static Vector paths = new Vector(10,5);
	static Vector ips = new Vector(10,5);
	static boolean flag = false;
	
	public FileTransferer(ServerSocket server, String path, InetAddress ip)
	{
	  super("FileTransferer");
	  this.server = server;
	  addList(path,ip);
	}
	
	synchronized void addList(String path, InetAddress ip)
	{
		while(flag)
			try {wait();} catch(Exception e){;}
		flag = true;
		paths.add(path);
		ips.add(ip);
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
		flag = false;
		notify();
	}
	
	public void run()
	{
		boolean b = true;
		int n = 0;
		int c = 0;
		String name;
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
			fichero.createNewFile();
			out = new FileOutputStream(fichero);
        
			while((c = in.read())!=-1) out.write(c);

			out.flush();
			
		}
		catch(Exception e)
		{;}
		//comprobar MD5;
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
		{;}
		
		return n;
	}
}