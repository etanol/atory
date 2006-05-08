/*
 * ParserXML.java
 *
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

    Netfolder net;
    Storage storage;

    private XmlPullParserFactory factory;
    private XmlSerializer serializer;

    /**
    * Constructora del parser sin parámetros.
    */
    public ParserXML() throws IOException, XmlPullParserException
    {
        this(null,null);
    }

    
    /**
     *Constructora del parser especificando instáncias.
     *
     *@Param n instáncia de Netfolder 
     *@Param s instáncia de Storage
     */
    public ParserXML(Netfolder n, Storage s) throws IOException,
           XmlPullParserException
    {

       net = n;
       storage = s;

       factory = XmlPullParserFactory.newInstance(
               System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);

       factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
       serializer = factory.newSerializer();      
    }

    
    /**
     * Consultora del campo Netfolder. 
     */ 
    public Netfolder getNetfolder() { return net; }

    
    /**
     * Consultora del campo Storage. 
     */ 
    public Storage getStorage() { return storage; } 

    
    /**
     * Parsea todo tipo de documentos en xml.
     *
     * Las funcionalidades del programa, con su correspondiente representación en xml son los siguientes:
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
     *         <File  name="juas.jpg"  md5="dgfjhgiueh" size="56">
     *             <host> .....  </host>
     *             .....
     *         </File>
     *         <File  name="juas.jpg"  md5="dgfjhgiueh" size="56">
     *             <host> .....  </host>
     *             .....
     *         </File>
     *         .....
     *     </FilesList>
     *
     * AÑADIR FICHEROS / ELIMINAR FICHEROS
     *
     *     <AddFiles>  |  <DelFiles>
     *         <File  name="juas.jpg"  md5="dgfjhgiueh" size="56">
     *             <host> .....  </host>
     *         </File>
     *         <File  name="juas.jpg"  md5="dgfjhgiueh" size="56">
     *             <host> .....  </host>
     *         </File>
     *         ....
     *     </AddFiles>  |  </DelFiles>
     *
     * AÑADIR HOSTS / ELIMINAR HOSTS
     *
     *     <AddHosts>  |  <DelHosts>
     *         <host> ..... </host>
     *         <host> ..... </host>
     *         ....
     *     </AddHosts>  |  </DelHosts>
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
    private void parsea(String xml)
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
                parseaListaHosts(xpp);
            else if(s.equals("FilesList"))
                parseaListaFicheros(xpp);
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

    
    private void parseaNuevaConexion(XmlPullParser xpp) throws IOException,
            XmlPullParserException, Exception
            {
                //Sabemos que esta bien formado, y accedemos directamente al TEXT
                String host;

                xpp.next();
                xpp.next();
                host = xpp.getText();

                xmlListaFicheros(host);
                xmlListaHosts(host);
                net.addHost(host);
            }

    
    private void parseaListaHosts(XmlPullParser xpp) throws IOException,
            XmlPullParserException
            {
                int tipoEvento = xpp.next();
                while(tipoEvento != XmlPullParser.END_TAG && xpp.getName().equals("HostList"))
                {
                    if(tipoEvento == XmlPullParser.TEXT)
                        net.addHost(xpp.getText());

                    tipoEvento = xpp.next();
                }
            }

    
    private void parseaListaFicheros(XmlPullParser xpp) throws IOException,
            XmlPullParserException, Exception
            {
                int tipoEvento = xpp.next();
                Fichero f = new Fichero();

                while(tipoEvento != XmlPullParser.END_TAG && xpp.getName().equals("FilesList"))
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
                            storage.addFichero(f);
                    }
                    tipoEvento = xpp.next();
                }
            }

    
    private void parseaAnadirFicheros(XmlPullParser xpp) throws 
        IOException, XmlPullParserException, Exception
        {
            int tipoEvento = xpp.next();
            Fichero f = new Fichero();

            while(tipoEvento != XmlPullParser.END_TAG && xpp.getName().equals("AddFiles"))
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
                        storage.addFichero(f);
                }
                tipoEvento = xpp.next();
            }
        }

    
    private void parseaEliminarFicheros(XmlPullParser xpp) throws 
        IOException, XmlPullParserException
        {
            int tipoEvento = xpp.next();
            String nombre="",host="";  


            while(tipoEvento != XmlPullParser.END_TAG && xpp.getName().equals("DelFiles"))
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
                        storage.delFichero(nombre,host);
                }
                tipoEvento = xpp.next();
            }
        }

    
    private void parseaAnadirHosts(XmlPullParser xpp) throws IOException,
            XmlPullParserException
            {
                int tipoEvento = xpp.next();
                while(tipoEvento != XmlPullParser.END_TAG && xpp.getName().equals("AddHost"))
                {
                    if(tipoEvento == XmlPullParser.TEXT)
                        net.addHost(xpp.getText());

                    tipoEvento = xpp.next();
                }
            }

    
    private void parseaEliminarHosts(XmlPullParser xpp) throws IOException,
            XmlPullParserException
            {
                int tipoEvento = xpp.next();
                while(tipoEvento != XmlPullParser.END_TAG && xpp.getName().equals("DelHost"))
                {
                    if(tipoEvento == XmlPullParser.TEXT)
                    {
                        net.removeHost(xpp.getText());
                        storage.delHost(xpp.getText());
                    }

                    tipoEvento = xpp.next();
                }
            }

    
    private void parseaPeticionFichero(XmlPullParser xpp)
    {
            int tipoEvento = xpp.next();
            String nombre="",host="";  


            while(tipoEvento != XmlPullParser.END_TAG && xpp.getName().equals("ReqFile"))
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
                        storage.delFichero(nombre,host);
                }
                tipoEvento = xpp.next();
            }

    }

    
    private Fichero parseaAtributosFichero(XmlPullParser xpp)
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
    public void xmlNuevaConexion(String host) throws IOException, Exception
    {  
        StringWriter documento = new StringWriter();
        serializer.setOutput( documento ); 

        serializer.startTag("","NewConnection");
        serializer.text("\n");
        serializer.startTag("", "host")
            .text(net.whoAmI())
            .endTag("", "host");
        serializer.text("\n");
        serializer.endTag("", "NewConnection");
        net.sendXml(host,documento.toString());

    }

    /**
     * Función que crea un documento xml con la lista de ficheros compartidos.
     */
    public void xmlListaFicheros(String host) throws IOException, Exception
    {
        Fichero f;
        Enumeration e;

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag("", "FilesList")
            .text("\n");


        for(Enumeration lista = storage.getListaFicheros(); lista.hasMoreElements();)
        {
            f = (Fichero)lista.nextElement();
            serializer.startTag("", "File")
                .attribute("","name",f.getNombre())
                .attribute("","md5",f.getMd5())
                .attribute("","size",java.lang.String.valueOf(f.getTamano()))
                .text("\n");
            e = f.getHosts();
            while(e.hasMoreElements())
            {
                serializer.startTag("","host")
                    .text((String)e.nextElement())
                    .endTag("","host")
                    .text("\n");
            }
            serializer.endTag("", "File")
                .text("\n");
        }
        serializer.endTag("", "FilesList");
        net.sendXml(host,documento.toString());

    }

    /**
     * Función que crea un documento xml con las IPs de los hosts que conforman 
     * la red.
     */
    public void xmlListaHosts(String host) throws IOException, Exception
    {
        int n;
        Vector v = net.getListaHosts();

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag("", "HostsList")
            .text("\n");

        n = v.size();
        for(int i=0;i<n;i++)
        {
            serializer.startTag("", "host")
                .text((String)v.elementAt(i))
                .endTag("", "host")
                .text("\n");
        }
        serializer.endTag("", "HostsList");
        net.sendXml(host,documento.toString());
    }


    /**
     * Función que crea un documento xml con una petición de transferencia de un 
     * fichero "fichero" que tiene el host "host".
     <ReqFile>
     <File  name="juas.jpg">
     <host> ..... </host>
     </File>
     </ReqFile>
     *
     */
    public void xmlReqFichero(String fichero, String host) throws IOException,
           Exception
           {
               StringWriter documento = new StringWriter();
               serializer.setOutput( documento );

               serializer.startTag("", "ReqFile")
                   .text("\n");
               serializer.startTag("", "File")
                   .attribute("","name",fichero)
                   .text("\n");
               serializer.startTag("", "host")
                   .text(net.whoAmI())
                   .endTag("", "host")
                   .text("\n");
               serializer.endTag("", "File")
                   .text("\n");
               serializer.endTag("", "ReqFile");

               net.sendXml(host,documento.toString());
           }


    /**
     * <AddFiles>  |  <DelFiles>
     *    <File  name="juas.jpg"  md5="dgfjhgiueh" size="56">
     *       <host> .....  </host>
     *    </File>
     *    <File  name="juas.jpg"  md5="dgfjhgiueh" size="56">
     *       <host> .....  </host>
     *    </File>
     *     ....
     * </AddFiles>  |  </DelFiles>
     * 
     * @param ficheros Vector con objetos Fichero
     */
    public void xmlAnadirFicheros(Vector ficheros) throws IOException, Exception
    {
        Fichero f;
        int n;
        String ip_local;

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag("", "AddFiles")
            .text("\n");

        n = ficheros.size();
        ip_local = net.whoAmI();

        for(int i=0;i<n;i++)
        {
            f = (Fichero)ficheros.elementAt(i);
            serializer.startTag("", "File")
                .attribute("","name",f.getNombre())
                .attribute("","md5",f.getMd5())
                .attribute("","size",java.lang.String.valueOf(f.getTamano()))
                .text("\n");
            serializer.startTag("", "host")
                .text(ip_local)
                .endTag("", "host")
                .text("\n");
            serializer.endTag("", "File");
        }
        serializer.endTag("", "AddFiles");
        net.sendMessage(documento.toString());
    }


    public void xmlEliminarFicheros(Vector ficheros) throws IOException, Exception
    {
        Fichero f;
        int n;
        String ip_local;

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag("", "DelFiles")
            .text("\n");

        n = ficheros.size();
        ip_local = net.whoAmI();

        for(int i=0;i<n;i++)
        {
            f = (Fichero)ficheros.elementAt(i);
            serializer.startTag("", "File")
                .attribute("","name",f.getNombre())
                .attribute("","md5",f.getMd5())
                .attribute("","size",java.lang.String.valueOf(f.getTamano()))
                .text("\n");
            serializer.startTag("", "host")
                .text(ip_local)
                .endTag("", "host")
                .text("\n");
            serializer.endTag("", "File");
        }
        serializer.endTag("", "AddFiles");
        net.sendMessage(documento.toString());
    }


    /**
       <AddHosts>  |  <DelHosts>
       <host> ..... </host>
       <host> ..... </host>
       ....
       </AddHosts>  |  </DelHosts>
     * 
     */

    public void xmlAnadirHost(String host) throws IOException, Exception
    {

        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag("", "AddHost")
            .text("\n");
        serializer.startTag("", "host")
            .text(host)
            .endTag("","host")
            .text("\n"); 
        serializer.endTag("", "AddHost");

        net.sendMessage(documento.toString());
    }


    public void xmlEliminarHost(String host) throws IOException, Exception
    {
        StringWriter documento = new StringWriter();
        serializer.setOutput( documento );

        serializer.startTag("", "DelHost")
            .text("\n");
        serializer.startTag("", "host")
            .text(host)
            .endTag("","host")
            .text("\n"); 
        serializer.endTag("", "DelHost");

        net.sendMessage(documento.toString());
    }

}
