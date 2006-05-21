package atory.net;

import java.net.*;
import java.io.*;
import java.util.*;
import atory.xml.*;
/**
 * Clase encargada de tratar las conexiones al puerto de control de XML.
 */
public class xmlThread extends Thread
{
   Socket socket = null;
   
   public xmlThread(Socket conexion)
   {
      super("xmlThread");
      socket = conexion;
   }
   
   public void run()
   {
      String xml = new String();
      DataInputStream input;
	  try
	  {
		try
		{
			input = (new DataInputStream(
					socket.getInputStream()));
			xml = input.readUTF();
         regularip(xml);
			ParserXML.parsea(xml);
		}
		catch(Exception e)
		{
			return;
		}
		finally
		{
			socket.close();
		}
	  }
	  catch(Exception e)
	  {
		return;
	  }
   }
	
   private static String regularip(String data)
   {
     String cadena = null;
     boolean b = false;
     try{
	  System.out.println("xml a convertir: " + data);
     Enumeration e1 = NetworkInterface.getNetworkInterfaces();
	  System.out.println("x");
     while(e1.hasMoreElements())
     {
		   NetworkInterface netface = (NetworkInterface)
            e1.nextElement();
			Enumeration e2 = netface.getInetAddresses();

		  while(e2.hasMoreElements())
		  {

			  if(!b)
			  {
				  cadena +=((InetAddress)e2.nextElement()).toString();
				  b = true;
			  }
			  cadena +="|"+((InetAddress)e2.nextElement()).toString();
		  }
		  System.out.println("Cadena "+ cadena);
	  } 
	  System.out.println("Ultima cadena "+ cadena);
	  data = data.replaceAll(cadena,"127.0.0.1");
	  System.out.println("xml reconvertido: " + data);
     return data;
     }
     catch(Exception e)
     {
		  System.out.println("error");
	  }
     return data;
   }


}
