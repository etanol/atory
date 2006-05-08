package atory.net;
/**
 * Netfolder - Clase encargada de enviar i/o recibir datos a trav茅s de la red
 * $Revision$
 */

import java.io.*;
import java.net.*;

/**
 *  Clase encargada de enviar i/o recibir datos a trav茅s de la red
 */
public class Netfolder
{
   static final int XMLPORT = 3330;
   static final int DATAPORT = 3331;
   static final int INTENTOS = 3;
   static final int TIME_WAIT = 30000;

   /**
    * Funci贸n encargada de enviar documentos XML a trav茅s de la red.
    *
    * @param ipdestino Direcci贸n destino.
    * @param data Documento XML a enviar.
    */
   public static void sendXml(String ipdestino, String data) throws Exception
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
         throw new Exception("Direcci胣 IP mal formada");
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
            dest.close();
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
    * Funci贸n encargada de enviar ficheros a trav茅s de la red.
    * 
    * @param ipdestino Direcci贸n destino.
    * @param fichero Fichero a enviar.
    */
   public static void sendFile(String ipdestino, File fichero) throws Exception
   {
      int i = 0;
      int c;
      Socket dest = null; 
      InetAddress destino;
      BufferedInputStream buf = null;
      try
      {
         destino = getIp(ipdestino);
      }
      catch(UnknownHostException e)
      {
         throw new Exception("Direcci胣 IP mal formada");
      }
      while(i<INTENTOS)
      {
         try
         {
            dest = new Socket(destino , DATAPORT);
            OutputStream output = dest.getOutputStream();
            FileInputStream in = new FileInputStream(fichero);
            buf = new BufferedInputStream(in);
            // Lectura y envio del archivo
            while((c = buf.read())!=-1)
               output.write(c);
            
            output.flush();
            dest.close();
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
    * Funci贸n encargada de recibir un archivo y escribirlo en disco.
    * @param file Nombre del archivo.
    */

   public static void getFile(String file) throws Exception
   {
      int c;
      File fichero = new File(file);
      Socket origen = null;
      ServerSocket server = null;
      FileOutputStream out = null; 
      try
      {
         server = new ServerSocket(DATAPORT);
         // tiempo de espera de conexi贸n.
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
         out.close();
         origen.close();
         server.close();

      }
      catch(Exception e)
      {
         throw new Exception("Error en la conexi贸n");
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
    * Transforma una string ip en un tipo ip (InetAddress).
    * @param destino Direcci贸n ip.
    */
   private static InetAddress getIp(String destino) throws UnknownHostException
   {
      byte[] ip = new byte[4];
      String[] bytes = destino.split("[.]",4);
      ip[0] = (new Integer(Integer.parseInt(bytes[0]))).byteValue();
      ip[1] = (new Integer(Integer.parseInt(bytes[1]))).byteValue();
      ip[2] = (new Integer(Integer.parseInt(bytes[2]))).byteValue();
      ip[3] = (new Integer(Integer.parseInt(bytes[3]))).byteValue();
      return InetAddress.getByAddress(ip);
   }
}
