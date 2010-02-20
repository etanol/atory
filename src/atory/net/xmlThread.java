/*
 * xmlThread.java
 *
 * $Revision$
 */
package atory.net;

import java.net.*;
import java.io.*;
import java.util.*;
import atory.xml.*;

/**
 * Clase encargada de tratar las conexiones al puerto de control de XML. Por
 * cada hilo se atiende un único mensaje XML por el puerto de control.
 */
public class xmlThread extends Thread
{
   Socket socket = null;
   
   /**
    * Constructor. Crea un nuevo xmlThread, como es evidente.
    */
   public xmlThread(Socket conexion)
   {
      super("xmlThread");
      socket = conexion;
   }
   
   /**
    * Procesar el mensaje XML. Todo lo que llegue por el puerto de control se
    * tomará como un mensaje de Atory. Ni más ni menos.
    */
   public void run()
   {
      String xml = new String();
      DataInputStream input;
	  try
	  {
		try
		{
            int temp = (socket.getLocalAddress().toString()).indexOf('/');
            
			input = (new DataInputStream(
					socket.getInputStream()));
			xml = input.readUTF();
      		xml = regularip(xml, ((socket.getLocalAddress()).toString()).substring(temp+1));
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
	
   private static String regularip(String data, String ip)
    {
     String cadena = null;
     boolean b = false;
     try{
	  data = data.replaceAll(ip, Netfolder.whoAmI());
     return data;
     }
     catch(Exception e)
     {;}
     return data;
   }

}
