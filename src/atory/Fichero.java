/*
 * Fichero.java
 *
 * $Revision$
 */
package atory;

import atory.net.Netfolder;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Random;

/**
 * Representación de fichero utilizada por Atory. En esta clase meteremos lo que
 * entendemos por fichero compartido, además de los métodos de ayuda para
 * manipular las listas internas.
 */
public class Fichero
{
	private String  nombre;
	private String  md5;
	private long    tamano;
    private long    fecha;
    private Vector  hosts;

    /**
     * Constructor por defecto. Construye un fichero sin datos.
     */
	public Fichero ()
	{
        this ("", "", 0, 0);
	}

    /**
     * Constructor con datos.
     *
     * @param nom Nombre.
     * @param enc MD5 digest.
     * @param tam Tamaño en bytes.
     */
	public Fichero (String nom, String enc, long tam, long fec)
	{
		nombre = nom;
		md5    = enc;
		tamano = tam;
        fecha  = fec;
        hosts  = new Vector ();
	}

    /**
     * Añadir un propietario a este fichero. Si el host ya existe la lista no se
     * modifica.
     *
     * @param host El nombre de la máquina (IP) que se añadirá.
     */
    public void addHost (String host)
    {
        if (!hosts.contains (host))
            hosts.addElement (host);
    }

    /**
     * Eliminar un propietaro de este fichero. Si el host NO existe la lista no
     * se modifica.
     *
     * @param host El nombre de la máquina (IP) que se añadirá.
     */
    public void delHost (String host)
    {
        int i = hosts.indexOf (host);
        if (i > -1)
            hosts.removeElementAt (i);
    }

    /**
     * Unir listas de propietarios. Si tanto este fichero como el parámetro file
     * son compatibles (es decir, coinciden en nombre y MD5) se van insertando
     * los nombres de host nuevos en nuestra lista.
     *
     * @param  file      El fichero con el que nos queremos fusionar.
     * @throws Exception Si los ficheros no son compatibles.
     */
    public void merge (Fichero file) throws Exception
    {
        Enumeration e;

        if (!this.nombre.equals (file.nombre))
            throw new Exception ("Los nombres no coinciden");
        if (!this.md5.equals (file.md5))
            throw new Exception ("Los ficheros no son iguales");
        // Aquí ya es seguro que son iguales
        e = file.hosts.elements ();
        while (e.hasMoreElements ())
            this.addHost ((String) e.nextElement ());
    }

    /**
     * Test de existencia del fichero. Si el fichero no tiene ningún propietario
     * es que ha dejado de existir en la red.
     *
     * @return true si el fichero tiene algún propietario, false sino.
     */
    public boolean exists ()
    {
        return !hosts.isEmpty ();
    }

    /**
     * Test de localidad del fichero. Con este método podemos comprobar si el
     * fichero es accesible directamente a través de nuestro disco duro.
     *
     * @return true si somos uno de los propietarios del fichero, false sino o
     *              si se produjo alguna excepción.
     */
    public boolean isLocal ()
    {
        String my_ip = "";
        try {
            my_ip = Netfolder.whoAmI();
        } catch (Exception ex) {
            return false;
        }
        return hosts.contains (my_ip);
    }

    /**
     * Obtener un propietario aleatorio de la lista de propietarios. De esta
     * forma podemos repartir aleatoriamente la carga de cada peer en la red.
     *
     * @param rand Generador de números aleatorios para seleccionar un índice
     *             del vector.
     * @return     Una cadena que representa a uno de los propietarios de este
     *             fichero.
     */
    public String getRandomHost (Random rand)
    {
        int i = rand.nextInt (hosts.size ());
        return (String) hosts.elementAt (i);
    }

    /**
     * Obtener un iterador para recorrer la lista de propietarios.
     *
     * @return Un iterador de tipo Enumeration sobre la lista interna.
     */
    public Enumeration getHosts ()
    {
        return hosts.elements ();
    }

    /*
     * Métodos GET
     */
	public String getNombre () { return nombre; }
	public String getMd5    () { return md5; }
	public long   getTamano () { return tamano; }
    public long   getFecha  () { return fecha; }

    /*
     * Métodos SET
     */
	public void setNombre (String nom) { nombre = nom; }
	public void setMd5    (String enc) { md5    = enc; }
	public void setTamano (long tam)   { tamano = tam; }
    public void setFecha  (long fec)   { fecha  = fec; }
}

