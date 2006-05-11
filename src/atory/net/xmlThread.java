package atory.net;

import java.net.*;
import java.io.*;
import atory.xml.*;
/**
 * Clase encargada de tratar las conexiones al puerto de control de XML.
 */
public class xmlThread extends Thread
{
   Socket socket = null;
   ParserXML parser = null;
   
   public xmlThread(Socket conexion, ParserXML xmlp)
   {
      super("xmlThread");
      socket = conexion;
	  parser = xmlp;
   }
   
   public void run()
   {
      String xml = new String();
      String inputLine;
	  try
	  {
		try
		{
			BufferedReader in = new BufferedReader( new InputStreamReader(
					socket.getInputStream()));
			while ((inputLine = in.readLine()) != null)
				xml+=inputLine;
         
			parser.parsea(xml);
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
}
