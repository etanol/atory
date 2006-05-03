/*
 * Storage.java
 *
 * $Revision$
 */
package atory;

//import atory.xml.*;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Almacén de información sobre ficheros compartidos. Aquí se almacena la
 * información tanto de ficheros locales como de remotos.
 */
public class Storage {

    // ParserXML parser;
    Hashtable table;

    /**
     * Constructor por defecto. Crea una lista vacía.
     */
    public Storage ()
    {
        table = new Hashtable();
    }

    /**
     * Vacía la lista interna de ficheros. Haya lo que haya, todo se evacúa para
     * quedar sin ningún fichero en la lista.
     */
    public void listaVacia ()
    {
        table = new Hashtable();
    }

    /**
     * Añadir un fichero o ampliar su información. Puede ser que el fichero ya
     * exista, en tal caso hay que fusionar las listas de hosts.
     *
     * @param  new_file  Objeto Fichero que será insertado o cuya información se
     *                   utilizará para actualizar el existente.
     * @throws Exception Cuando no se pueden unir los ficheros.
     */
    public void addFichero (Fichero new_file) throws Exception
    {
        Fichero file;

        file = (Fichero) table.get( new_file.getNombre() );
        if( file == null )
            table.put( new_file.getNombre(), new_file );
        else 
            file.merge( new_file );
    }

    /**
     * Eliminar la participación de un host en un fichero. Cuando un fichero se
     * queda sin ningún host que lo tenga el fichero se elimina de la lista
     * porque ya no existe en la red.
     *
     * @param nombre Nombre del fichero que se elimina.
     * @param host   Máquina que eliminó el fichero de su disco duro.
     */
    public void delFichero (String nombre, String host)
    {
        Fichero file;

        file = (Fichero) table.get( nombre );
        if( file != null ) {
            file.delHost( host );
            if( ! file.exists() )
                table.remove( nombre );
        }
    }

    /**
     * Eliminar todas las participaciones de un host.
     *
     * @param host El nombre (IP) del host que se está retirando de la red.
     */
    public void delHost (String host)
    {
        Fichero file;
        Enumeration e;

        e = table.elements();
        while( e.hasMoreElements() ) {
            file = (Fichero) e.nextElement();
            file.delHost( host );
            if( ! file.exists() )
                table.remove( file.getNombre() );
        }
    }

    /**
     * Solicitar la transferencia de un fichero. Seleccionamos un propietario al
     * azar y le enviamos una petición.
     *
     * @param  nombre    El nombre del fichero a transferir.
     * @throws Exception Si el fichero con dicho nombre no está en la lista.
     */
    public void reqFichero (String nombre) throws Exception
    {
        Fichero file;

        file = (Fichero) table.get( nombre );
        if( file == null )
            throw new Exception( "Fichero no encontrado" );
    }

    /**
     * Obtener un iterador para recorrer la lista de ficheros.
     *
     * @return Un iterador de tipo Enumeration sobre la lista interna.
     */
    public Enumeration getFicheros ()
    {
        return table.elements();
    }

}

