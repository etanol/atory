package atory.xml;
/*
 * JPListaFicheros - Descripción breve de la clase
 *
 * Descripción detallada (por favor rellenad).
 *
 * $Revision$
 */

import org.xmlpull.v1.XmlPullParserException;
import java.util.Vector;
import java.io.IOException;

public class JPListaFicheros
{
	public static void main (String args[])
		throws XmlPullParserException, IOException
	{
        ListaFicheros lf = new ListaFicheros("cosarara.xml");
		Vector v = lf.getListaFicheros();
		int n = v.size();
		Fichero f;

		for(int i=0;i<n;i++)
		{
            f = (Fichero)v.elementAt(i);
			
			System.out.print("Nombre del archivo: " + f.getNombre() + " ");
			System.out.print("Md5: " + f.getMd5() + " ");
			System.out.println("Tamano: " + f.getTamano() + " ");
			System.out.println("Comentario del fichero: " + f.getComentario()+ "\n");
		}
		
	}
}
