/*
 * Storage.java
 *
 * $Revision$
 */
package atory;

import atory.xml.*;
import atory.fs.*;
import atory.gui.*;
import atory.net.*;
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
     */
    public static void addFichero (Fichero new_file)
    {
        Fichero file;
        Vector  fs = new Vector ();

        file = (Fichero) table.get (new_file.getNombre ());
        if (file == null) {
            table.put (new_file.getNombre (), new_file);
            MainWindow.anyadirFichero (new_file);
            fs.addElement (new_file);
        } else {
            file.merge (new_file);
            MainWindow.cambiarUbicacion (file);
            fs.addElement (file);
        }
    }

    /**
     * Añadir una lista de ficheros locales o ampliar su información. Hace lo
     * mismo que addFichero() pero para una lista y sólo para ficheros locales.
     * Si alguno de los ficheros no se puede unir se descarta. 
     *
     * @param lista Iterador de objetos Fichero en el directorio local.
     */
    public static void addFicheros (Enumeration lista)
    {
        Fichero file, new_file;
        Vector added = new Vector();

        while (lista.hasMoreElements ()) {
            new_file = (Fichero) lista.nextElement ();
            file     = (Fichero) table.get (new_file.getNombre ());
            if (file == null) {
                // El fichero no estaba, lo insertamos
                table.put (new_file.getNombre (), new_file);
                MainWindow.anyadirFichero (new_file);
                added.addElement (new_file);
            } else {
                // El fichero estaba, intentamos fusionar ambas versiones
                file.merge (new_file);
                MainWindow.cambiarUbicacion (file);
                added.addElement (file);
            }
        }

        try {
            ParserXML.xmlAnadirFicheros (added);
        } catch (Exception ex) {
            Errlog.printex ("Storage.addFicheros()", ex);
        }
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
            MainWindow.cambiarUbicacion (file);
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
    public static void delFicheros (Enumeration lista)
    {
        Fichero old_file, file;
        Vector removed = new Vector ();

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
        try {
            ParserXML.xmlEliminarFicheros (removed);
        } catch (Exception ex) {
            Errlog.printex ("Storage.delFicheros()", ex);
        }
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
            MainWindow.cambiarUbicacion (file);
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
     * @param  secu      Si true indica que se utilice un canal cifrado de datos
     *                   para la transmisión.
     * @throws Exception Si el fichero con dicho nombre no está en la lista.
     */
    public static void reqFichero (String nombre, boolean secu) throws Exception
    {
        Fichero file;
        Vector  fs = new Vector ();

        file = (Fichero) table.get (nombre);
        if (file == null)
            throw new Exception ("Fichero no encontrado");
        if (!file.isLocal ()) {
            if (secu)
                ParserXML.xmlReqSecureFichero (file.getNombre (),
                                               file.getRandomHost (rand),
                                               file.getTamano ());
            else
                ParserXML.xmlReqFichero (file.getNombre (),
                                         file.getRandomHost (rand),
                                         file.getTamano ());
            fs.addElement (file);
            ParserXML.xmlAnadirFicheros (fs);
        }
    }

    /**
     * Comprueba la integridad de un fichero. Verifica que el contenido de un
     * fichero corresponde con el MD5 indicado en su meta-información. Además,
     * actualiza la lista de ficheros locales para poder refrescar la interfaz
     * más rápidamente.
     *
     * @param  fd   El fichero a verificar.
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
            } catch (Exception ex) {
                Errlog.printex ("Storage.checkIntegrity()", ex);
            }
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

    /**
     * Desconectar.
     */
    public static void disconnect () throws Exception
    {
        ParserXML.xmlEliminarHost (Netfolder.whoAmI ());
        Netfolder.reset ();
        listaVacia ();
        Disco.merge ();
    }
}

