import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.util.Vector;

public class ListaFicheros
{
   Vector listaFicheros;

   public ListaFicheros()
   {
	   listaFicheros = new Vector(10,5);
   }

   public ListaFicheros(String fichero)
	   throws XmlPullParserException, IOException
   {
	   listaFicheros = new Vector(10,5);
	   parsearDocumento(fichero);
   }

   public Vector getListaFicheros()
   {
       return listaFicheros;
   }

   
	private void parsearDocumento(String fichero) 
		throws XmlPullParserException, IOException
	{
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
			System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);

		factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);      

		XmlPullParser xpp = factory.newPullParser();

		xpp.setInput ( new FileReader ( fichero ) ); //como nos llegara el documento xml?
		procesaDocumento(xpp);
	}

	private void procesaDocumento(XmlPullParser xpp) 
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
     
	}
}