/*
 * Storage.java
 *
 * $Revision$
 */
package atory;

import atory.xml.*;
import atory.fs.*;
import atory.gui.*;
import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

/**
 * Almacén de información sobre ficheros compartidos. Aquí se almacena la
 * información tanto de ficheros locales como de remotos.
 */
public class Storage {

    private static boolean   iniciado = false;
    private static Hashtable table;
    private static Random    rand; // Para pedir hosts aleatorios.

    /**
     * Constructor (ignorado).
     */
    public Storage () {}
    
    /**
     * Inicializador de la clase.
     */
    public static void init ()
    {
        if (iniciado) return;

        iniciado = true;
        table    = new Hashtable ();
        rand     = new Random ();
    }

    /**
     * Vacía la lista interna de ficheros. Haya lo que haya, todo se evacúa para
     * quedar sin ningún fichero en la lista.
     */
    public static void listaVacia ()
    {
        table = new Hashtable ();
        MainWindow.eliminarTodos ();
    }

    /**
     * Añadir un fichero o ampliar su información. Puede ser que el fichero ya
     * exista, en tal caso hay que fusionar las listas de hosts.
     *
     * @param  new_file  Objeto Fichero que será insertado o cuya información se
     *                   utilizará para actualizar el existente.
     * @throws Exception Cuando no se pueden unir los ficheros.
     */
    public static void addFichero (Fichero new_file) throws Exception
    {
        Fichero file;

        file = (Fichero) table.get (new_file.getNombre ());
        if (file == null) {
            table.put (new_file.getNombre (), new_file);
            MainWindow.anyadirFichero (new_file);
        } else {
            MainWindow.eliminarFichero (file);
            file.merge (new_file);
            MainWindow.anyadirFichero (file);
        }

    }

    /**
     * Añadir una lista de ficheros locales o ampliar su información. Hace lo
     * mismo que addFichero() pero para una lista y sólo para ficheros locales.
     * Si alguno de los ficheros no se puede unir se descarta. 
     *
     * @param lista Iterador de objetos Fichero en el directorio local.
     */
    public static void addFicheros (Enumeration lista) throws Exception
    {
        Fichero file, new_file;
        Vector added = new Vector();

        while (lista.hasMoreElements ()) {
            new_file = (Fichero) lista.nextElement ();
            file     = (Fichero) table.get (new_file.getNombre ());
            if (file == null) {
                // El fichero no estaba, lo insertamos
                table.put (new_file.getNombre (), new_file);
                added.addElement (new_file);
                MainWindow.anyadirFichero (new_file);
            } else {
                try {
                    // El fichero estaba, intentamos fusionar ambas versiones
                    MainWindow.eliminarFichero (file);
                    file.merge (new_file);
                    MainWindow.anyadirFichero (file);
                    added.addElement (file);
                } catch (Exception ex) {}
            }
        }
        ParserXML.xmlAnadirFicheros (added);
    }

    /**
     * Eliminar la participación de un host en un fichero. Cuando un fichero se
     * queda sin ningún host que lo tenga el fichero se elimina de la lista
     * porque ya no existe en la red.
     *
     * @param nombre Nombre del fichero que se elimina.
     * @param host   Máquina que eliminó el fichero de su disco duro.
     */
    public static void delFichero (String nombre, String host)
    {
        Fichero file;

        file = (Fichero) table.get (nombre);
        if (file != null) {
            file.delHost (host);
            if (!file.exists ()) {
                table.remove (nombre);
                MainWindow.eliminarFichero (file);
            }
        }
    }

    /**
     * Elimina nuestra participación en los ficheros de la lista. Sólo se invoca
     * al producirse un evento de este tipo localmente.
     *
     * @param lista Iterador de objetos Fichero que ya no existen localmente.
     */
    public static void delFicheros (Enumeration lista) throws Exception
    {
        Fichero old_file, file;
        Vector removed = new Vector();

        while (lista.hasMoreElements ()) {
            old_file = (Fichero) lista.nextElement ();
            file     = (Fichero) table.get (old_file.getNombre ());
            if (file != null) {
                // El fichero está, así que hay que borrarlo de nuestra
                // participación local.
                file.setLocal (false);
                MainWindow.cambiarUbicacion (file);
                // ¿Se ha eliminado de la red?
                if (!file.exists ()) {
                    table.remove (file.getNombre ());
                    MainWindow.eliminarFichero (file);
                }
                removed.addElement (file);
            }
        }
        ParserXML.xmlEliminarFicheros (removed);
    }

    /**
     * Eliminar todas las participaciones de un host.
     *
     * @param host El nombre (IP) del host que se está retirando de la red.
     */
    public static void delHost (String host)
    {
        Fichero file;
        Enumeration e;

        e = table.elements ();
        while (e.hasMoreElements ()) {
            file = (Fichero) e.nextElement ();
            file.delHost (host);
            if (!file.exists ()) {
                table.remove (file.getNombre ());
                MainWindow.eliminarFichero (file);
            }
        }
    }

    /**
     * Solicitar la transferencia de un fichero. Seleccionamos un propietario al
     * azar y le enviamos una petición. Si el fichero es local no hacemos nada
     * pues ya lo tenemos.
     *
     * @param  nombre    El nombre del fichero a transferir.
     * @throws Exception Si el fichero con dicho nombre no está en la lista.
     */
    public static void reqFichero (String nombre) throws Exception
    {
        Fichero file;

        file = (Fichero) table.get (nombre);
        if (file == null)
            throw new Exception ("Fichero no encontrado");
        if (!file.isLocal ())
            ParserXML.xmlReqFichero (file.getNombre (),
                                     file.getRandomHost (rand),
                                     file.getTamano ());
    }

    /**
     * Comprueba la integridad de un fichero. Verifica que el contenido de un
     * fichero corresponde con el MD5 indicado en su meta-información. Además,
     * actualiza la lista de ficheros locales para poder refrescar la interfaz
     * más rápidamente.
     *
     * @param nombre El nombre del fichero en el directorio compartido.
     * @return true si el fichero es correcto, false sino.
     */
    public static boolean checkIntegrity (File fd)
    {
        Fichero fich;
        String md5;

        fich = (Fichero) table.get (fd.getName ());
        if (fich != null) {
            try {
                md5 = MD5.fromFile (fd);
                if (md5.equals (fich.getMd5 ())) {
                    fich.setLocal (true);
                    Disco.updateDownloaded (fd, md5);
                    MainWindow.cambiarUbicacion (fich);
                    return true;
                }
            } catch (Exception ex) {}
        }
        return false;
    }

    /**
     * Obtener un iterador para recorrer la lista de ficheros.
     *
     * @return Un iterador de tipo Enumeration sobre la lista interna.
     */
    public static Enumeration getListaFicheros ()
    {
        return table.elements ();
    }

}

