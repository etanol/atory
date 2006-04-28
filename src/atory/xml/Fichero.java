package atory.xml;
/*
 * Fichero - Descripción breve de la clase
 *
 * Descripción detallada (por favor rellenad).
 *
 * $Revision$
 */

import java.lang.String;

//queda por decidir si los sets son void o int segun el tratamiento de errores

public class Fichero
{
	String nombre;
	String md5; //falta mirar si hi ha algun tipus especial
	float tamano;
	String comentario;

	public Fichero()
	{
	   nombre = "";
	   md5 = "";
	   tamano = 0;
	   comentario = "";
	}

	public Fichero(String nom, String enc, float tam, String com)
	{
		nombre = nom;
		md5 = enc;
		tamano = tam;
		comentario = com;
	}

	public String getNombre()
	{
	    return nombre;
	}

	public void setNombre(String nom)
	{
        nombre = nom;      
	}

	public String getMd5()
	{
		return md5;
	}

	public void setMd5(String enc)
	{
		md5 = enc;      
	}

	public float getTamano()
	{
		return tamano;
	}

	public void setTamano(float tam)
	{
		tamano = tam;      
	}

	public String getComentario()
	{
		return comentario;
	}

	public void setComentario(String com)
	{
		comentario = com;      
	}

}
