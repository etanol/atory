package atory.net;
/**
 * Netfolder - Clase encargada de enviar i/o recibir datos a través de la red
 * $Revision$
 */

import java.io.*;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;
import java.util.*;
import java.nio.*;
import atory.xml.*;
import atory.*;

/**
 *  Clase encargada de enviar i/o recibir datos a través de la red
 */
public class Netfolder
{
   static final int XMLPORT = 9001;
   static final int DATAPORT = 9002;
	static final int SECUREPORT = 9003;
   static final int INTENTOS = 3;
   static final int TIME_WAIT = 30000;
   static boolean ipcalc = false;
   static String myip;
   static Vector hosts = new Vector(10,5);
   static Vector hoststr = new Vector(10,5);
   static boolean listening = true;
   static ParserXML parser;
   static String pathname = System.getProperty ("sharedir");
   static ServerSocket dataserver;
   static ServerSocket controlserver;
	static ServerSocket secureserver;  

   /**
    * Constructor (ignorado).
    */
   public Netfolder () {}

   /**
    * Inicializadora de la clase. Se encarga de inicializar los puertos de datos
    * y de control. Si están ocupados (cualquiera de los dos) se lanza una
    * excepción.
	*/
   public static void init () throws Exception
   {
     try
	 {
		 dataserver = new ServerSocket(DATAPORT);
		 controlserver = new ServerSocket(XMLPORT);
		 ServerSocketFactory ssocketFactory = SSLServerSocketFactory.getDefault();
		 secureserver = ssocketFactory.createServerSocket(SECUREPORT);	
	 }
	  catch(Exception e) 
	  {		 
		  if(controlserver != null)
			  //throw new Exception("Error en SSLSocket");
			  throw e;

		  if(dataserver != null)
			  dataserver.close();

		  throw new Exception("Puertos en uso");
	  }
   }

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
	 * Introduce el pathname de referencia para leer y dejar los ficheros.
	 *
	 * @param path Directorio de trabajo.
	 */
	public static void setPathname(String path)
   {
	  pathname=path;
   }

	/**
	 * Funcin encargada de resetear la lista de hosts.
	 */
	public static void reset()
	{
		hosts.removeAllElements();
		hoststr.removeAllElements();
	}

   /**
    * Función encargada de enviar documentos XML a través de la red.
    *
    * @param ipdestino Dirección destino.
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
         System.out.println ("sendXML: ip (" + ipdestino + "), data (" + data + ")");
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
				int temp = (dest.getLocalAddress().toString()).indexOf('/');
      		data = regularip(data, ((dest.getLocalAddress()).toString()).substring(temp+1));
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
	
	private static String regularip(String data, String ip)
   {
     String cadena = null;
     boolean b = false;
     try{
	  System.out.println("xml a convertir: " + data+ " IP "+ ip);
	  data = data.replaceAll(whoAmI(),ip);
	  System.out.println("xml reconvertido: " + data);
     return data;
     }
     catch(Exception e)
     {;}
     return data;
   }


   /**
    * Función encargada de enviar ficheros a través de la red.
    * 
    * @param ipdestino Dirección destino.
    * @param f Fichero a enviar.
    */
	public static void sendFile(String ipdestino, String f) throws Exception
	{
		int i = 0;
		int c;
		Socket dest = null; 
		InetAddress destino;
		BufferedInputStream buf = null;
		System.err.println ("RUTA: "+ pathname+f);
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
	 * Función encargada de enviar ficheros a través de la red de forma segura.
	 * 
	 * @param ipdestino Dirección destino.
	 * @param f Fichero a enviar.
	 */
	public static void sendSecureFile(String ipdestino, String f) throws Exception
	{
		int i = 0;
		int c;
		Socket dest = null; 
		InetAddress destino;
		BufferedInputStream buf = null;
		//TODO: quitar chivato
		System.err.println ("RUTA SSl: "+ pathname+f);
		File fichero = new File((pathname+f));
		try
		{
			destino = getIp(ipdestino);
		}
		catch(UnknownHostException e)
		{
			throw new Exception("Dirección IP mal formada");
		}
      SocketFactory socketFactory = SSLSocketFactory.getDefault();
		while(i<INTENTOS)
		{
			try
			{
				dest = socketFactory.createSocket(destino , SECUREPORT);
            System.err.println("SSL abierto");
				OutputStream output = dest.getOutputStream();
				FileInputStream in = new FileInputStream(fichero);
				buf = new BufferedInputStream(in);
				// Lectura y envío del archivo
				while((c = buf.read())!=-1)
					output.write(c);

				output.flush();
            System.err.println("he acabado de enviar");
				i = INTENTOS;
			} 
			catch(Exception e)
			{
            System.err.println("Envias?");
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
	 * Función encargada de recibir ficheros a través de la red de forma segura.
	 * 
	 * @param ipdestino Dirección destino.
	 * @param f Fichero a enviar.
	 */
	public static void getSecureFile(String file, String host, long size) throws Exception
	{
		System.err.println ("RUTA SEGURA: " +pathname+file);
     (new FileTransferer(secureserver, (pathname+file), getIp(host),size)).start();
	}

	/**
	 * Función encargada de recibir un archivo y escribirlo en disco.
	 *
	 * @param file Nombre del archivo.
	 * @deprecated El nuevo método getFile soporta múltiples conexiones al mismo puerto.
	 */

	public static void getFile(String file) throws Exception
	{
      int c;
      System.err.println ("RUTA: " +pathname+file);
      File fichero = new File((pathname+file));
      Socket origen = null;
      FileOutputStream out = null; 
      try
      {
	     
         // tiempo de espera de conexión.
         dataserver.setSoTimeout(TIME_WAIT);
         try
         {
            origen = dataserver.accept();
         }
         catch(InterruptedIOException e ) 
         {
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
         if(out!=null)
            out.close();
      }
         
   }
   
   /**
    * Método para obtener ficheros. Cuando es invocado crea un thread a la espera
	* de la conexión indicada.
	*
	* @param file Nombre del archivo a recibir.
	* @param host Ip del cliente.
	*/
   public static void getFile(String file, String host, long size) throws Exception
   {
      System.err.println ("RUTA: " +pathname+file);
     (new FileTransferer(dataserver, (pathname+file), getIp(host),size)).start();
   }

   /**
    * Recibe documentos XML. Función bloqueante.
    */
   public static void getXml() throws Exception
   {

      while(listening)
         new xmlThread(controlserver.accept()).start();

     controlserver.close();
   }
  
  /**
   * Manda el mensaje xml a todas los hosts conocidos.
   * @param xml Mensaje a enviar.
   */
   public static void sendMessage(String xml)
   {
      // Medicion de tiempo
		Date fin;
      Date inicio = new Date();
		for(int i=0; i<hosts.size(); i++)
		{
			try
			{
				sendprivate((InetAddress)hosts.get(i),xml);
			}
			catch(Exception e)
			{
				Storage.delHost((String)hoststr.get(i));
				removeHost((String)hoststr.get(i));
            //TODO:borrar host de la pantalla
			}
		}
		fin = new Date();
      
      long ttotal=fin.getTime()-inicio.getTime();//milisegundos
      System.out.println(ttotal);
      
   }
   
   private static void sendprivate(InetAddress destino, String data) throws Exception
   {
      int i = 0;
	  Socket dest = null;
      DataOutputStream flujo = null; 
      while(i<INTENTOS)
      {
         try
         {
            dest = new Socket(destino , XMLPORT);
				int temp = (dest.getLocalAddress().toString()).indexOf('/');
      		data = regularip(data,((dest.getLocalAddress()).toString()).substring(temp+1));
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
   public static String whoAmI() throws Exception
   {
      InetAddress local = InetAddress.getLocalHost(); 
      return (local.getHostAddress()).toString();
   }

   /**
    * Devuelve la lista de hosts conectados.
    */
   public static Vector getListaHosts()
   {
      return hoststr;
   }
   
   /**
    * Añade un host a la lista de hosts conectados.
    * @param host Ip del host.
    */
   public static void addHost(String host) throws UnknownHostException
   {
      int i;
      InetAddress ip = getIp(host);
      for(i=0;i<hosts.size() && !host.equals((String) hoststr.get(i)) ;i++);
      if(hosts.size()==i)
      {
         hosts.addElement(ip);
         hoststr.addElement(host);
      }
      System.err.println("LISTA IP's: " + hoststr);
   }

   /**
    * Borra un host de las listas de hosts.
    * @param host Ip del host a borrar.
    */
	public static void removeHost(String host)
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
