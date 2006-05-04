/*
 * ParserXML.java
 *
 * $Revision$
 */
package atory;

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

   public ParserXML() throws IOException, XmlPullParserException
   {

      net = new Netfolder();
	   storage = new Storage();

	   factory = XmlPullParserFactory.newInstance(
		   System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
      /* Es necessari??? */
	   factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true); 
      
      serializer = factory.newSerializer();     
   }

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

	public Netfolder getNetfolder() { return net; }

	public Storage getStorage() { return storage; } 

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
			parseaNuevaConexion(xpp);//llamar a funciones del ivan para enviar lista ips i lista ficheros a la nueva ip
		else if(s.equals("HostsList"))
			parseaListaHosts(xpp); //aÃ±adir cada ip con addHost del dani
		else if(s.equals("FilesList"))
			parseaListaFicheros(xpp);//aÃ±adir ficheros al isaac
		else if(s.equals("AddFiles"))
			parseaAnadirFicheros(xpp);
		else if(s.equals("DelFiles"))
			parseaEliminarFicheros(xpp);
		else if(s.equals("AddHosts"))
			parseaAnadirHosts(xpp);
		else if(s.equals("DelHosts"))
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
	   Fichero f = new Fichero();    // Tocat: no compilava //

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
		Fichero f = new Fichero(); // Tocat: No compilava //

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
		String nombre="",host="";  // Tocat: no compilava //


		while(tipoEvento != XmlPullParser.END_TAG && xpp.getName().equals("DelFiles"))
		{
			if(tipoEvento == XmlPullParser.START_TAG)
			{
				if(xpp.getName().equals("File"))
					nombre = xpp.getAttributeValue(0); //cogemos directamente solo el valor del nombre, los otros no importan
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
				case 2:  f.setTamano(Long.decode(valor));  break;
				default: break;
			
			}
		}
		return f;
	}


////////////////////////////////////////////////PRIVADAS IVAN///////////////////////////////////////////////////
	


/**
* Función que crea un documento xml con una petición de transferencia de un 
* fichero "fichero" que tiene el host "host".
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
   net.sendXML(host,documento.toString());

}


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
   net.sendXML(host,documento.toString());
   
}

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
   net.sendXML(host,documento.toString());
}


/*
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

   net.sendXML(host,documento.toString());
}


/*
<AddFiles>  |  <DelFiles>
        <File  name="juas.jpg"  md5="dgfjhgiueh" size="56">
            <host> .....  </host>
        <File  name="juas.jpg"  md5="dgfjhgiueh" size="56">
            <host> .....  </host>
        ....
    </AddFiles>  |  </DelFiles>
 * 
 * @params: Vector con objetos Fichero
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


/*
   <AddHosts>  |  <DelHosts>
        <host> ..... </host>
        <host> ..... </host>
        ....
    </AddHosts>  |  </DelHosts>
 * 
 */

public void xmlAnadirHosts(String host, Vector ips) throws IOException, Exception
{
   
// No estic segur, però diria que només és per afegir/eliminar una ip //
   
   StringWriter documento = new StringWriter();
   serializer.setOutput( documento );

   serializer.startTag("", "AddHost")
      .text("\n");
   //Falta
   serializer.endTag("", "AddHost");

   net.sendMessage(documento.toString());
}


public void xmlEliminarHosts(String host, Vector ips) throws IOException, Exception
{
   StringWriter documento = new StringWriter();
   serializer.setOutput( documento );

   serializer.startTag("", "DelHost")
      .text("\n");
   //Falta
   serializer.endTag("", "DelHost");

   net.sendMessage(documento.toString());
}




/*
	private void procesaDocumento(XmlPullParser xpp) 
		throws XmlPullParserException, IOException
	{
		
		
		int i=0;
		do 
		{
			if(eventType == XmlPullParser.START_TAG) 
			{
				System.out.println(" start: " + i);
				
				if(xpp.getName().equals("Fichero"))
				  anadirComentario(xpp);
				else if(xpp.getName().equals("Eliminar"))
				  eliminarFichero(xpp);
				else if(xpp.getName().equals("Modificar"))
			}
			else if(eventType == XmlPullParser.TEXT)
				System.out.println(" text: " + i);
			else if(eventType == XmlPullParser.END_TAG)
				System.out.println(" end: " + i);
			//	anadirComentario(xpp);
			eventType = xpp.next();
			i++;
		} while (eventType != XmlPullParser.END_DOCUMENT);
	}

	private void anadirComentario(XmlPullParser xpp)
		throws XmlPullParserException, IOException
	{
		int eventType = xpp.getEventType();

			if(eventType == XmlPullParser.START_TAG) 
			{
				System.out.println(" start: ");
				if(xpp.getName().equals("Anadir"))
				 anadirFichero(xpp);
				 else if(xpp.getName().equals("Eliminar"))
				 eliminarFichero(xpp);
				 else if(xpp.getName().equals("Modificar"))
			}
			else 
				System.out.println("que putada");

		
	}*/

	/*private void procesaDocumento(XmlPullParser xpp) 
		throws XmlPullParserException, IOException
	{
		   boolean b=false;
		   int eventType = xpp.getEventType();
		   do 
		   {
			   if(eventType == XmlPullParser.START_TAG) 
			   {
				   if(xpp.getName().equals("Fichero"))
				   {
					   b = true;
					   anadirFichero(xpp);
				   }
			   }
			   else if(eventType == XmlPullParser.TEXT && b)
				   anadirComentario(xpp);
			   eventType = xpp.next();
		   } while (eventType != XmlPullParser.END_DOCUMENT);
    }


    private void anadirFichero(XmlPullParser xpp)
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
				case 0:  f.setNombre(valor);
					     break;
				case 1:  f.setMd5(valor);
					     break;
				case 2:  f.setTamano(Float.parseFloat(valor));
					     break;
				default: break;
			
			}
			System.out.println(xpp.getAttributeValue(i));
		}
	
        listaFicheros.addElement(f);
	}

	private void anadirComentario(XmlPullParser xpp)
		throws XmlPullParserException, IOException
	{
		if(!xpp.getText().equals(""))
		{
			Fichero f;
			 System.out.println(xpp.getText());
			 f = (Fichero)listaFicheros.lastElement();
			 listaFicheros.remove(listaFicheros.size()-1);
			 f.setComentario(xpp.getText());
			 listaFicheros.addElement(f);
		}
		  //((Fichero)listaFicheros.lastElement()).setComentario(xpp.getText());
     
	}*/
}
