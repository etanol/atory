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
   
   public xmlThread(Socket conexion)
   {
      super("xmlThread");
      socket = conexion;
   }
   
   public void run()
   {
      String xml = new String();
      String inputLine;
      try
      {
         BufferedReader in = new BufferedReader( new InputStreamReader(
                socket.getInputStream()));
         while ((inputLine = in.readLine()) != null)
            xml+=inputLine;
         
         XmlParser parser = new XmlParser();
         parser.parser(xml);
      }
      finally
      {
         socket.close();
      }
   }
}
