package atory.net;
/**
 * Netfolder - Clase encargada de enviar i/o recibir datos a través de la red
 * $Revision$
 */

import java.io.*;
import java.net.*;
import java.util.*;
import atory.xml.*;

/**
 *  Clase encargada de enviar i/o recibir datos a través de la red
 */
public class Netfolder
{
   static final int XMLPORT = 3330;
   static final int DATAPORT = 3331;
   static final int INTENTOS = 3;
   static final int TIME_WAIT = 30000;
   static boolean ipcalc = false;
   static String myip;
   static Vector hosts = new Vector(10,5);
   static Vector hoststr = new Vector(10,5);
   static boolean listening = true;
   static ParserXML parser;
   static String pathname;

   /**
    * Función encargada de activar/desactivar el puerto de control.
    *
    * @param listen Valor del parámetro listening.
    */
   public static void setListening(boolean listen)
   {
      listening = listen;
   }
   
   /**
    * Set de la referencia al parser.
	*
	* @param xmlp Parser.
	*/
   public void setParserXML(ParserXML xmlp)
   {
	  parser = xmlp;
   }
   
   /**
    * Introduce el pathname de referencia para leer y dejar los ficheros.
	*
	* @param path Directorio de trabajo.
	*/
   public void setPathname(String path)
   {
	  pathname=path;
   }

   /**
    * Función encargada de enviar documentos XML a través de la red.
    *
    * @param ipdestino Dirección destino.
    * @param data Documento XML a enviar.
    */
   public void sendXml(String ipdestino, String data) throws Exception
   {
      int i = 0;
      InetAddress destino;
      Socket dest = null;
      DataOutputStream flujo = null; 
      try
      {
         destino = getIp(ipdestino);
      }
      catch(UnknownHostException e)
      {
         throw new Exception("Dirección IP mal formada");
      }
      while(i<INTENTOS)
      {
         try
         {
            dest = new Socket(destino , XMLPORT);
            OutputStream output = dest.getOutputStream();
            flujo = new DataOutputStream(output);
            flujo.writeUTF(data);
            flujo.flush();
            i = INTENTOS;
         } 
         catch(Exception e)
         {
            if(++i== INTENTOS)
               throw new Exception("Error al conectar"); 
         }
         finally
         {
            if(dest!=null)
               dest.close();
            if(flujo!=null)
               flujo.close();
         }
      }
   }

   /**
    * Función encargada de enviar ficheros a través de la red.
    * 
    * @param ipdestino Dirección destino.
    * @param f Fichero a enviar.
    */
   public void sendFile(String ipdestino, String f) throws Exception
   {
      int i = 0;
      int c;
      Socket dest = null; 
      InetAddress destino;
      BufferedInputStream buf = null;
	  File fichero = new File((pathname+f));
      try
      {
         destino = getIp(ipdestino);
      }
      catch(UnknownHostException e)
      {
         throw new Exception("Dirección IP mal formada");
      }
      while(i<INTENTOS)
      {
         try
         {
            dest = new Socket(destino , DATAPORT);
            OutputStream output = dest.getOutputStream();
            FileInputStream in = new FileInputStream(fichero);
            buf = new BufferedInputStream(in);
            // Lectura y envío del archivo
            while((c = buf.read())!=-1)
               output.write(c);
            
            output.flush();
            i = INTENTOS;
         } 
         catch(Exception e)
         {
            if(++i== INTENTOS)
               throw new Exception("Error al conectar"); 
         }
         finally
         {
            if(dest!=null)
               dest.close();
            if(buf!=null)
               buf.close();
         }
      }
   }

   /**
    * Función encargada de recibir un archivo y escribirlo en disco.
    *
	* @param file Nombre del archivo.
    */

   public void getFile(String file) throws Exception
   {
      int c;
      File fichero = new File(file);
      Socket origen = null;
      ServerSocket server = null;
      FileOutputStream out = null; 
      try
      {
         server = new ServerSocket(DATAPORT);
         // tiempo de espera de conexión.
         server.setSoTimeout(TIME_WAIT);
         try
         {
            origen = server.accept();
         }
         catch(InterruptedIOException e ) 
         {
            server.close();
            throw new Exception("Time out!");
         }
         InputStream in = origen.getInputStream();
         fichero.createNewFile();
         out = new FileOutputStream(fichero);
         while((c = in.read())!=-1)
            out.write(c);

         out.flush();

      }
      catch(Exception e)
      {
         throw new Exception("Error en la conexión");
      }
      finally
      {
         if(origen!=null)
            origen.close();
         if(server!=null)
            server.close();
         if(out!=null)
            out.close();
      }
         
   }

   /**
    * Recibe documentos XML.
    */
   public void getXml() throws Exception
   {
      Socket origen = null;
      ServerSocket server = null;
      FileOutputStream out = null; 
      try
      {
         server = new ServerSocket(XMLPORT);

      }
      catch(Exception e)
      {
         throw new Exception("Puerto en uso");
      }
      while(listening)
         new xmlThread(server.accept(),parser).start();

      server.close();
   }
  
  /**
   * Manda el mensaje xml a todas los hosts conocidos.
   *
   * @param xml Mensaje a enviar.
   */
   public void sendMessage(String xml)
   {
		for(int i=0; i<hosts.size(); i++)
		{
			try
			{
				sendprivate((InetAddress)hosts.get(i),xml);
			}
			catch(Exception e)
			{
				removeHost((String)hoststr.get(i));
			}
		}
			
   }
   
   private void sendprivate(InetAddress destino, String data) throws Exception
   {
      int i = 0;
	  Socket dest = null;
      DataOutputStream flujo = null; 
      while(i<INTENTOS)
      {
         try
         {
            dest = new Socket(destino , XMLPORT);
            OutputStream output = dest.getOutputStream();
            flujo = new DataOutputStream(output);
            flujo.writeUTF(data);
            flujo.flush();
            i = INTENTOS;
         } 
         catch(Exception e)
         {
            if(++i== INTENTOS)
               throw new Exception("Error al conectar"); 
         }
         finally
         {
            if(dest!=null)
               dest.close();
            if(flujo!=null)
               flujo.close();
         }
      }
   }

   /**
    * Retorna la ip local.
    */
   public String whoAmI() throws Exception
   {
      InetAddress local = InetAddress.getLocalHost(); 
       return (local.getHostAddress()).toString();
   }

   /**
    * Devuelve la lista de hosts conectados.
    */
   public Vector getListaHosts()
   {
      return hoststr;
   }
   
   /**
    * Añade un host a la lista de hosts conectados.
    * @param host Ip del host.
    */
   public void addHost(String host) throws UnknownHostException
   {
      int i;
      InetAddress ip = getIp(host);
      for(i=0;i<hosts.size() && ip.equals(hosts.get(i)) ;i++);
      if(hosts.size()==i)
      {
         hosts.addElement(ip);
         hoststr.addElement(host);
      }
   }

   /**
    * Borra un host de las listas de hosts.
    * @param host Ip del host a borrar.
    */
   public void removeHost(String host)
   {
	try
	{
	  for(int i=0; i<hoststr.size(); i++)
	  {
		if((hoststr.get(i)).equals(host))
		{
			hosts.remove(i);
			hoststr.remove(i);
			break;
		}
	  }
	}
	catch(Exception e)
	{
		return;
	}
   }

   /**
    * Transforma una string ip en un tipo ip (InetAddress).
    * @param destino Dirección ip.
    */
   private static InetAddress getIp(String destino) throws UnknownHostException
   {
      byte[] ip = new byte[4];
      String[] bytes; 
      try
      {
         bytes = destino.split("[.]",4);
      }
      catch(NullPointerException e)
      {
         throw new UnknownHostException();
      }
      ip[0] = (new Integer(Integer.parseInt(bytes[0]))).byteValue();
      ip[1] = (new Integer(Integer.parseInt(bytes[1]))).byteValue();
      ip[2] = (new Integer(Integer.parseInt(bytes[2]))).byteValue();
      ip[3] = (new Integer(Integer.parseInt(bytes[3]))).byteValue();
      return InetAddress.getByAddress(ip);
   }
}
