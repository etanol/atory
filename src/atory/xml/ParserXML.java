/*
 * ParserXML.java
 * Clase encargada de parsear, interpretar y serializar los comandos que 
 * se envian por la red.
 * $Revision$
 */
package atory.xml;

import atory.*;
import atory.net.*;

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

       factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
       serializer = factory.newSerializer();      
    }

    
    /**
     * Parsea todo tipo de documentos en xml.
     */
    
     /* Las funcionalidades del programa, con su correspondiente representación 
     * en xml son los siguientes: 
     *
     * AÑADIR CONEXIÓN 
     * 
     *     <NewConnection> 
     *         <host> 10.10.10.10 </host> 
     *     </NewConnection> 
     * 
     * ENVIO LISTA IPS 
     *
     *     <HostsList>
     *         <host> .....  </host>
     *         <host> .....  </host>
     *         .....
     *     </HostsList>
     * 
     * ENVIO LISTA FICHEROS
     *
     *     <FilesList>
     *         <File  name="juas.jpg"  md5="d2f4125ae3" size="56">
     *             <host> .....  </host>
     *             .....
     *         </File>
     *         <File  name="juas.jpg"  md5="d2f4125ae3" size="56">
     *             <host> .....  </host>
     *             .....
     *         </File>
     *         .....
     *     </FilesList>
     *
     * AÑADIR FICHEROS / ELIMINAR FICHEROS
     *
     *     <AddFiles>  |  <DelFiles>
     *         <File  name="juas.jpg"  md5="d2f4125ae3" size="56">
     *             <host> .....  </host>
     *         </File>
     *         <File  name="juas.jpg"  md5="d2f4125ae3" size="56">
     *             <host> .....  </host>
     *         </File>
     *         ....
     *     </AddFiles>  |  </DelFiles>
     *
     * AÑADIR HOSTS / ELIMINAR HOSTS
     *
     *     <AddHost>  |  <DelHost>
     *         <host> ..... </host>
     *     </AddHost>  |  </DelHost>
     *
     * PETICIÓN FICHERO
     *
     *     <ReqFile>
     *        <File  name="juas.jpg">
     *            <host> ..... </host>
     *        </File>
     *     </ReqFile>
     *     
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
                parseaAnadirHosts(xpp);
            else if(s.equals("FilesList"))
                parseaAnadirFicheros(xpp);
            else if(s.equals("AddFiles"))
                parseaAnadirFicheros(xpp);
            else if(s.equals("DelFiles"))
                parseaEliminarFicheros(xpp);
            else if(s.equals("AddHost"))
                parseaAnadirHosts(xpp);
            else if(s.equals("DelHost"))
                parseaEliminarHosts(xpp);
            else //ReqFile
                parseaPeticionFichero(xpp);
        }

    
    private static void parseaNuevaConexion(XmlPullParser xpp) throws IOException,
            XmlPullParserException, Exception
            {
                //Sabemos que esta bien formado, y accedemos directamente al TEXT
                String host;

                xpp.next();
                xpp.next();
                host = xpp.getText();

                xmlListaFicheros(host);
                xmlListaHosts(host);
                Netfolder.addHost(host);
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

    
    private static void parseaPeticionFichero(XmlPullParser xpp) throws 
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
                        Netfolder.sendFile(host,nombre);
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
     */
    public static void xmlNuevaConexion(String host) throws IOException, Exception
    {  
        StringWriter documento = new StringWriter();
        serializer.setOutput( documento ); 

        serializer.startTag("","NewConnection");
        serializer.startTag("", "host")
            .text(Netfolder.whoAmI())
            .endTag("", "host");
        serializer.endTag("", "NewConnection");
        Netfolder.sendXml(host,documento.toString());

    }

    /**
     * Función que crea un documento xml con la lista de ficheros compartidos.
     */
    public static void xmlListaFicheros(String host) throws IOException, Exception
    {
        Fichero f;
        Enumeration e;

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag("", "FilesList");


        for(Enumeration lista = Storage.getListaFicheros(); lista.hasMoreElements();)
        {
            f = (Fichero)lista.nextElement();
            serializer.startTag("", "File")
                .attribute("","name",f.getNombre())
                .attribute("","md5",f.getMd5())
                .attribute("","size",java.lang.String.valueOf(f.getTamano()));
            e = f.getHosts();
            while(e.hasMoreElements())
            {
                serializer.startTag("","host")
                    .text((String)e.nextElement())
                    .endTag("","host");
            }
            serializer.endTag("", "File");
        }
        serializer.endTag("", "FilesList");
        Netfolder.sendXml(host,documento.toString());

    }

    /**
     * Función que crea un documento xml con las IPs de los hosts que conforman 
     * la red.
     */
    public static void xmlListaHosts(String host) throws IOException, Exception
    {
        int n;
        Vector v = Netfolder.getListaHosts();

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag("", "HostsList");

        n = v.size();
        for(int i=0;i<n;i++)
        {
            serializer.startTag("", "host")
                .text((String)v.elementAt(i))
                .endTag("", "host");
        }
        serializer.endTag("", "HostsList");
        Netfolder.sendXml(host,documento.toString());
    }


    /**
     * Función que crea un documento xml con una petición de transferencia de 
     * un fichero "fichero" que tiene el host "host".
     */
    public static void xmlReqFichero(String fichero, String host) throws IOException,
           Exception
           {
               StringWriter documento = new StringWriter();
               serializer.setOutput( documento );

               serializer.startTag("", "ReqFile");
               serializer.startTag("", "File")
                   .attribute("","name",fichero);
               serializer.startTag("", "host")
                   .text(Netfolder.whoAmI())
                   .endTag("", "host");
               serializer.endTag("", "File");
               serializer.endTag("", "ReqFile");

               Netfolder.sendXml(host,documento.toString());
           }


    /**
     * Función que crea un documento xml con los nuevos ficheros que pasan a 
     * estar compartidos.
     * @param ficheros Vector con objetos Fichero.
     */
    public static void xmlAnadirFicheros(Vector ficheros) throws IOException, Exception
    {
        Fichero f;
        int n;
        String ip_local;

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag("", "AddFiles");

        n = ficheros.size();
        ip_local = Netfolder.whoAmI();

        for(int i=0;i<n;i++)
        {
            f = (Fichero)ficheros.elementAt(i);
            serializer.startTag("", "File")
                .attribute("","name",f.getNombre())
                .attribute("","md5",f.getMd5())
                .attribute("","size",java.lang.String.valueOf(f.getTamano()));
            serializer.startTag("", "host")
                .text(ip_local)
                .endTag("", "host");
            serializer.endTag("", "File");
        }
        serializer.endTag("", "AddFiles");
        Netfolder.sendMessage(documento.toString());
    }

    /**
     * Función que crea un documento xml con los ficheros que dejan de estar 
     * compartidos.
     * @param ficheros Vector con objetos Fichero.
     */
    public static void xmlEliminarFicheros(Vector ficheros) throws IOException, Exception
    {
        Fichero f;
        int n;
        String ip_local;

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag("", "DelFiles");

        n = ficheros.size();
        ip_local = Netfolder.whoAmI();

        for(int i=0;i<n;i++)
        {
            f = (Fichero)ficheros.elementAt(i);
            serializer.startTag("", "File")
                .attribute("","name",f.getNombre())
                .attribute("","md5",f.getMd5())
                .attribute("","size",java.lang.String.valueOf(f.getTamano()));
            serializer.startTag("", "host")
                .text(ip_local)
                .endTag("", "host");
            serializer.endTag("", "File");
        }
        serializer.endTag("", "DelFiles");
        Netfolder.sendMessage(documento.toString());
    }


   /**
     * Función que crea un documento xml indicando el nuevo host "host" que 
     * entra a formar parte de la red.
     */
    public static void xmlAnadirHost(String host) throws IOException, Exception
    {

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag("", "AddHost");
        serializer.startTag("", "host")
            .text(host)
            .endTag("","host");
        serializer.endTag("", "AddHost");

        Netfolder.sendMessage(documento.toString());
    }

   /**
     * Función que crea un documento xml indicando el host "host" que ha caido 
     * de la red.
     */
    public static void xmlEliminarHost(String host) throws IOException, Exception
    {
        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag("", "DelHost");
        serializer.startTag("", "host")
            .text(host)
            .endTag("","host");
        serializer.endTag("", "DelHost");

        Netfolder.sendMessage(documento.toString());
    }

}
