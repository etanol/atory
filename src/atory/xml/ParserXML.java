/*
 * ParserXML.java
 *
 * $Revision$
 */
package atory.xml;

import atory.*;
import atory.net.*;
import atory.fs.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.util.Vector;
import org.xmlpull.v1.XmlSerializer;
import java.io.StringWriter;
import java.util.Enumeration;

/**
 * Clase encargada de parsear, interpretar y serializar los comandos que se
 * envian por la red.
 */
public class ParserXML
{
    private static XmlPullParserFactory factory;
    private static XmlSerializer serializer;

    /**
     * Constructor (ignorado).
     */
    public ParserXML() {}

    
    /**
     * Inicializadora del parser.
     */
    public static void init () throws IOException, XmlPullParserException
    {
       factory = XmlPullParserFactory.newInstance(
               System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);

       //factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
       serializer = factory.newSerializer();      
    }

    
    /**
     * Parsea todo tipo de documentos en xml.
     *
     * Las funcionalidades del programa, con su correspondiente representación 
     * en xml son los siguientes: 
     *
     * <P>AÑADIR CONEXIÓN 
     * 
     * <pre>
     *     &lt;NewConnection&gt;
     *         &lt;host&gt; 10.10.10.10 &lt;/host&gt; 
     *     &lt;/NewConnection&gt; 
     * </pre>
     * 
     * <P>ENVIO LISTA IPS 
     *
     * <pre>
     *     &lt;HostsList&gt;
     *         &lt;host&gt; .....  &lt;/host&gt;
     *         &lt;host&gt; .....  &lt;/host&gt;
     *         .....
     *     &lt;/HostsList&gt;
     * </pre>
     * 
     * <P>ENVIO LISTA FICHEROS
     *
     * <pre>
     *     &lt;FilesList&gt;
     *         &lt;File  name="juas.jpg"  md5="d2f4125ae3" size="56"&gt;
     *             &lt;host&gt; .....  &lt;/host&gt;
     *             .....
     *         &lt;/File&gt;
     *         &lt;File  name="juas.jpg"  md5="d2f4125ae3" size="56"&gt;
     *             &lt;host&gt; .....  &lt;/host&gt;
     *             .....
     *         &lt;/File&gt;
     *         .....
     *     &lt;/FilesList&gt;
     * </pre>
     *
     * <P>AÑADIR FICHEROS / ELIMINAR FICHEROS
     *
     * <pre>
     *     &lt;AddFiles&gt;  |  &lt;DelFiles&gt;
     *         &lt;File  name="juas.jpg"  md5="d2f4125ae3" size="56"&gt;
     *             &lt;host&gt; .....  &lt;/host&gt;
     *         &lt;/File&gt;
     *         &lt;File  name="juas.jpg"  md5="d2f4125ae3" size="56"&gt;
     *             &lt;host&gt; .....  &lt;/host&gt;
     *         &lt;/File&gt;
     *         ....
     *     &lt;/AddFiles&gt;  |  &lt;/DelFiles&gt;
     * </pre>
     *
     * <P>AÑADIR HOSTS / ELIMINAR HOSTS
     *
     * <pre>
     *     &lt;AddHost&gt;  |  &lt;DelHost&gt;
     *         &lt;host&gt; ..... &lt;/host&gt;
     *     &lt;/AddHost&gt;  |  &lt;/DelHost&gt;
     * </pre>
     *
     * <P>PETICIÓN FICHERO
     *
     * <pre>
     *     &lt;ReqFile&gt;
     *        &lt;File  name="juas.jpg"&gt;
     *            &lt;host&gt; ..... &lt;/host&gt;
     *        &lt;/File&gt;
     *     &lt;/ReqFile&gt;
     * </pre>
     * 
     * <P>PETICIÓN FICHERO SEGURO
     *
     * <pre>
     *     &lt;ReqSecureFile&gt;
     *        &lt;File  name="juas.jpg"&gt;
     *            &lt;host&gt; ..... &lt;/host&gt;
     *        &lt;/File&gt;
     *     &lt;/ReqSecureFile&gt;
     * </pre>
     *   
     * @param xml String que contiene el mensaje xml a parsear.
     * @throws 
     */
    
    public static void parsea(String xml)
        throws XmlPullParserException, IOException, Exception
        {
            String s;
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(xml));

            int eventType = xpp.getEventType();
            xpp.next(); //Para coger el primer START_TAG, ya que sino estamos en STAT_DOCUMENT

            s = xpp.getName();
            if(s.equals("NewConnection"))
                parseaNuevaConexion(xpp);
            else if(s.equals("HostsList"))
            {
                parseaAnadirHosts(xpp);
                xmlAnadirHost(Netfolder.whoAmI());
            }
            else if(s.equals("FilesList")) {
                Storage.listaVacia ();
                parseaAnadirFicheros(xpp);
                Disco.merge ();
            } else if(s.equals("AddFiles"))
                parseaAnadirFicheros(xpp);
            else if(s.equals("DelFiles"))
                parseaEliminarFicheros(xpp);
            else if(s.equals("AddHost"))
                parseaAnadirHosts(xpp);
            else if(s.equals("DelHost"))
                parseaEliminarHosts(xpp);
            else if(s.equals("ReqFile"))
                parseaPeticionFichero(xpp,false);
			   else //ReqSecureFile
				    parseaPeticionFichero(xpp,true);
        }

    
    private static void parseaNuevaConexion(XmlPullParser xpp) throws IOException,
            XmlPullParserException, Exception
            {
                //Sabemos que esta bien formado, y accedemos directamente al TEXT
                String host;

                xpp.next();
                xpp.next();
                host = xpp.getText();

                xmlListaHosts(host);
                xmlListaFicheros(host);
                //Netfolder.addHost(host);
            }

    


    
    private static void parseaAnadirFicheros(XmlPullParser xpp) throws 
        IOException, XmlPullParserException, Exception
        {
            int tipoEvento = xpp.next();
            Fichero f = new Fichero();

            while(tipoEvento != XmlPullParser.END_DOCUMENT)
            {
				if(tipoEvento == XmlPullParser.START_TAG)
				{
					if(xpp.getName().equals("File"))
						f = parseaAtributosFichero(xpp);
				}
				else if(tipoEvento == XmlPullParser.TEXT)
					f.addHost(xpp.getText());
				else if(tipoEvento == XmlPullParser.END_TAG)
				{
					if(xpp.getName().equals("File"))
						Storage.addFichero(f);
				}
                tipoEvento = xpp.next();
            }
        }

    
    private static void parseaEliminarFicheros(XmlPullParser xpp) throws 
        IOException, XmlPullParserException
        {
            int tipoEvento = xpp.next();
            String nombre="",host="";  


            while(tipoEvento != XmlPullParser.END_DOCUMENT)
            {
                if(tipoEvento == XmlPullParser.START_TAG)
                {
                    if(xpp.getName().equals("File"))
                        nombre = xpp.getAttributeValue(0); //cogemos directamente solo el valor del nombre
                }
                else if(tipoEvento == XmlPullParser.TEXT)
                    host = xpp.getText();
                else if(tipoEvento == XmlPullParser.END_TAG)
                {
                    if(xpp.getName().equals("File"))
                        Storage.delFichero(nombre,host);
                }
                tipoEvento = xpp.next();
            }
        }
    
    private static void parseaAnadirHosts(XmlPullParser xpp) throws IOException,
            XmlPullParserException
            {
                int tipoEvento = xpp.next();
                while(tipoEvento != XmlPullParser.END_DOCUMENT)
                {
                    if(tipoEvento == XmlPullParser.TEXT)
                        Netfolder.addHost(xpp.getText());

                    tipoEvento = xpp.next();
                }
            }

    
    private static void parseaEliminarHosts(XmlPullParser xpp) throws IOException,
            XmlPullParserException
            {
                int tipoEvento = xpp.next();
                while(tipoEvento != XmlPullParser.END_DOCUMENT)
                {
                    if(tipoEvento == XmlPullParser.TEXT)
                    {
                        Netfolder.removeHost(xpp.getText());
                        Storage.delHost(xpp.getText());
                    }

                    tipoEvento = xpp.next();
                }
            }

    
    private static void parseaPeticionFichero(XmlPullParser xpp, boolean seguro) throws 
       XmlPullParserException, IOException, Exception
    {
            int tipoEvento = xpp.next();
            String nombre="",host="";  


            while(tipoEvento != XmlPullParser.END_DOCUMENT)
            {
                if(tipoEvento == XmlPullParser.START_TAG)
                {
                    if(xpp.getName().equals("File"))
                        nombre = xpp.getAttributeValue(0); //cogemos directamente solo el valor del nombre
                }
                else if(tipoEvento == XmlPullParser.TEXT)
                    host = xpp.getText();
                else if(tipoEvento == XmlPullParser.END_TAG)
                {
					if(xpp.getName().equals("File"))
					{
						if(seguro)
							Netfolder.sendSecureFile(host,nombre);
						else
						    //Netfolder.sendSecureFile(host,nombre);
						    Netfolder.sendFile(host,nombre);
					}
                }
                tipoEvento = xpp.next();
            }

    }

    
    private static Fichero parseaAtributosFichero(XmlPullParser xpp)
        throws XmlPullParserException, IOException
        {
            String valor;
            Fichero f = new Fichero();
            int n = xpp.getAttributeCount();

            for(int i=0;i<n;i++)
            {
                valor = xpp.getAttributeValue(i);
                switch(i)
                {
                    case 0:  f.setNombre(valor);  break;
                    case 1:  f.setMd5(valor);	break;
                    case 2:  f.setTamano(Long.decode(valor).longValue());  break;
                    default: break;

                }
            }
            return f;
        }


    
    //////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// SERIALIZACIÓN //////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////



    /**
     * Función que crea un documento xml que alerta al host "host" que nos 
     * conectamos.
     * @param host Host al que se le envia el xml.
     * @throws
     */
    public static void xmlNuevaConexion(String host) throws IOException, Exception
    {  
        StringWriter documento = new StringWriter();
        serializer.setOutput( documento ); 

        serializer.startTag(null,"NewConnection");
        serializer.startTag(null, "host")
            .text(Netfolder.whoAmI())
            .endTag(null, "host");
        serializer.endTag(null, "NewConnection");
        Netfolder.sendXml(host,documento.toString());

    }

    /**
     * Función que crea un documento xml con la lista de ficheros compartidos.
     * @param host Host al que se le envia el xml.
     * @throws
     */
    public static void xmlListaFicheros(String host) throws IOException, Exception
    {
        Fichero f;
        Enumeration e;

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag(null, "FilesList");


        for(Enumeration lista = Storage.getListaFicheros(); lista.hasMoreElements();)
        {
            f = (Fichero)lista.nextElement();
            serializer.startTag(null, "File")
                .attribute(null,"name",f.getNombre())
                .attribute(null,"md5",f.getMd5())
                .attribute(null,"size",java.lang.String.valueOf(f.getTamano()));
            e = f.getHosts();
            while(e.hasMoreElements())
            {
                serializer.startTag(null,"host")
                    .text((String)e.nextElement())
                    .endTag(null,"host");
            }
            serializer.endTag(null, "File");
        }
        serializer.endTag(null, "FilesList");
        Netfolder.sendXml(host,documento.toString());

    }

    /**
     * Función que crea un documento xml con las IPs de los hosts que conforman 
     * la red.
     * @param host Host al que se le envia el xml.
     * @throws
     */
    public static void xmlListaHosts(String host) throws IOException, Exception
    {
        int n;
        Vector v = Netfolder.getListaHosts();

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag(null, "HostsList");

        serializer.startTag(null, "host")
                .text(Netfolder.whoAmI())
                .endTag(null, "host");
 
        n = v.size();
        for(int i=0;i<n;i++)
        {
            serializer.startTag(null, "host")
                .text((String)v.elementAt(i))
                .endTag(null, "host");
        }
        serializer.endTag(null, "HostsList");
        Netfolder.sendXml(host,documento.toString());
    }


    /**
     * Función que crea un documento xml con una petición de transferencia de 
     * un fichero "fichero" que tiene el host "host".
     * @param fichero El nombre del fichero que se quiere descargar.
     * @param host Host al que se le envia el xml.
     * @param tamano Tamaño del fichero.
     * @throws
     */
    public static void xmlReqFichero(String fichero, String host, long tamano)
       throws IOException, Exception
           {
               StringWriter documento = new StringWriter();
               serializer.setOutput( documento );

               serializer.startTag(null, "ReqFile");
               serializer.startTag(null, "File")
                   .attribute(null,"name",fichero);
               serializer.startTag(null, "host")
                   .text(Netfolder.whoAmI())
                   .endTag(null, "host");
               serializer.endTag(null, "File");
               serializer.endTag(null, "ReqFile");

               Netfolder.getFile(fichero,host,tamano);
               Netfolder.sendXml(host,documento.toString());
           }


	/**
	 * Función que crea un documento xml con una petición de transferencia de 
	 * un fichero "fichero" que tiene el host "host". Esta función hace petición
	 * para que el fichero se envie con SSL de forma segura.
    * @param fichero El nombre del fichero que se quiere descargar.
    * @param host Host al que se le envia el xml.
    * @param tamano Tamaño del fichero.
    * @throws
	 */
    public static void xmlReqSecureFichero(String fichero, String host, long
         tamano) throws IOException, Exception
	{
		StringWriter documento = new StringWriter();
		serializer.setOutput( documento );

		serializer.startTag(null, "ReqSecureFile");
		serializer.startTag(null, "File")
			.attribute(null,"name",fichero);
		serializer.startTag(null, "host")
			.text(Netfolder.whoAmI())
			.endTag(null, "host");
		serializer.endTag(null, "File");
		serializer.endTag(null, "ReqSecureFile");


      Netfolder.getSecureFile(fichero,host,tamano);
		Netfolder.sendXml(host,documento.toString());
	}


    /**
     * Función que crea un documento xml con los nuevos ficheros que pasan a 
     * estar compartidos.
     * @param ficheros Vector con objetos Fichero que pasan a estar compartidos. 
     * @throws
     */
    public static void xmlAnadirFicheros(Vector ficheros) throws IOException, Exception
    {
        Fichero f;
        int n;
        String ip_local;

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag(null, "AddFiles");

        n = ficheros.size();
        ip_local = Netfolder.whoAmI();

        for(int i=0;i<n;i++)
        {
            f = (Fichero)ficheros.elementAt(i);
            serializer.startTag(null, "File")
                .attribute(null,"name",f.getNombre())
                .attribute(null,"md5",f.getMd5())
                .attribute(null,"size",java.lang.String.valueOf(f.getTamano()));
            serializer.startTag(null, "host")
                .text(ip_local)
                .endTag(null, "host");
            serializer.endTag(null, "File");
        }
        serializer.endTag(null, "AddFiles");
        Netfolder.sendMessage(documento.toString());
    }

    /**
     * Función que crea un documento xml con los ficheros que dejan de estar 
     * compartidos.
     * @param ficheros Vector con objetos Fichero que dejan de estar compartidos.
     * @throws
     */
    public static void xmlEliminarFicheros(Vector ficheros) throws IOException, Exception
    {
        Fichero f;
        int n;
        String ip_local;

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag(null, "DelFiles");

        n = ficheros.size();
        ip_local = Netfolder.whoAmI();

        for(int i=0;i<n;i++)
        {
            f = (Fichero)ficheros.elementAt(i);
            serializer.startTag(null, "File")
                .attribute(null,"name",f.getNombre())
                .attribute(null,"md5",f.getMd5())
                .attribute(null,"size",java.lang.String.valueOf(f.getTamano()));
            serializer.startTag(null, "host")
                .text(ip_local)
                .endTag(null, "host");
            serializer.endTag(null, "File");
        }
        serializer.endTag(null, "DelFiles");
        Netfolder.sendMessage(documento.toString());
    }


   /**
     * Función que crea un documento xml indicando el nuevo host "host" que 
     * entra a formar parte de la red.
     * @param host Host que se conecta a la red.
     * @throws
     */
    public static void xmlAnadirHost(String host) throws IOException, Exception
    {

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag(null, "AddHost");
        serializer.startTag(null, "host")
            .text(host)
            .endTag(null,"host");
        serializer.endTag(null, "AddHost");

        Netfolder.sendMessage(documento.toString());
    }

   /**
     * Función que crea un documento xml indicando el host "host" que ha caido 
     * de la red.
     * @param host Host que se desconecta a la red.
     * @throws
     */
    public static void xmlEliminarHost(String host) throws IOException, Exception
    {
        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag(null, "DelHost");
        serializer.startTag(null, "host")
            .text(host)
            .endTag(null,"host");
        serializer.endTag(null, "DelHost");

        Netfolder.sendMessage(documento.toString());
    }

}
