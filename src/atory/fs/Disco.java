/*
 * Disco.java
 *
 * $Revision$
 */
package atory.fs;

import atory.*;
import java.io.File;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;


/**
 * Clase para gestionar el acceso al directorio compartido. Por comodidad
 * mantendremos en una estructura de memoria la lista de ficheros locales del
 * último escaneo. Esa lista contendrá elementos de la clase Fichero, todos
 * ellos con el atributo isLocal() a verdadero.
 */
public class Disco {

    private static File      dirComp; 
    private static Hashtable localFiles;

    /**
     * Constructor (ignorado).
     */
    public Disco () {}

    /**
     * Inicializadora de la clase. Inicialmente escaneamos el directorio
     * compartido para tener una lista inicial con la que trabajar. Nótese que
     * únicamente se genera esa lista, NO se fusiona con el directorio
     * compartido (clase Storage).
     *
     * @throws Exception cuando ha habido algún problema con la entrada/salida
     *                   de disco.
     */
    public static void init () throws Exception
    {
        int i;
        File lista[];
        Fichero f;

        dirComp    = new File (System.getProperty ("sharedir"));
        localFiles = new Hashtable ();

        lista = dirComp.listFiles ();
        for (i = 0; i < lista.length; i++)
            if (lista[i].isFile ()) {
                f = new Fichero (lista[i].getName (),
                                 MD5.fromFile (lista[i]),
                                 lista[i].length (),
                                 lista[i].lastModified ());
                f.setLocal (true);
                localFiles.put (f.getNombre (), f);
            }
    }

    /**
     * Fusiona la lista local con la lista de ficheros remotos. Este método
     * únicamente se utiliza para fusionar la lista de ficheros locales con el
     * directorio compartido inicialmente.
     * <p>
     * Se ha separado de la inicialización para permitir que primero se procese
     * la lista de ficheros compartidos que viene de fuera y, posteriormente, se
     * añadan los ficheros locales para que se generen los mensajes de
     * notificación al resto de los hosts.
     *
     * @throws Exception simplemente propagada desde abajo.
     */
    public static void merge () throws Exception
    {
        Storage.addFicheros (localFiles.elements ());
    }

    /**
     * Escanea el directorio compartido en busca de cambios. Método invocado
     * periódicamente o bajo petición del usuario. Si ha habido cambios se
     * notifica a la clase Storage automáticamente.
     *
     * @throws Exception cuando ha habido algún problema con la entrada/salida
     *                   de disco.
     */
    public static void sync () throws Exception
    {
        int i;
        File lista[];
        Fichero f;
        Hashtable actual;
        Vector removed, added;
        Enumeration e;
        String md5;

        actual    = new Hashtable ();
        removed   = new Vector ();
        added     = new Vector ();

        // Construímos la lista de ficheros locales (sí, lo sé, esto es muy
        // lento)
        actual = (Hashtable) localFiles.clone ();

        // Recorrer el directorio compartido en busca de cambios
        lista = dirComp.listFiles ();
        for (i = 0; i < lista.length; i++)
            if (lista[i].isFile ()) {
                f = (Fichero) actual.remove (lista[i].getName ());
                if (f == null) {
                    // Nuevo fichero local
                    f = new Fichero (lista[i].getName (),
                                     MD5.fromFile (lista[i]),
                                     lista[i].length(),
                                     lista[i].lastModified ());
                    f.setLocal (true);
                    added.addElement (f);
                    localFiles.put (f.getNombre (), f);
                } else {
                    if (f.getFecha () < lista[i].lastModified ()) {
                        // Fichero local modificado. Esto implica eliminación y
                        // adición
                        removed.addElement (f);
                        localFiles.remove (f.getNombre ());

                        f = new Fichero (lista[i].getName (),
                                         MD5.fromFile (lista[i]),
                                         lista[i].length(),
                                         lista[i].lastModified ());
                        f.setLocal (true);
                        added.addElement (f);
                        localFiles.put (f.getNombre (), f);
                    } 
                }
            }

        // Si todavía quedan elementos en 'actual' es que han sido borrados del
        // directorio local.
        e = actual.elements ();
        while (e.hasMoreElements ()) {
            f = (Fichero) e.nextElement ();
            removed.addElement (f);
            localFiles.remove (f.getNombre ());
        }

        // Actualizar Storage, primero eliminados y luego añadidos
        if (removed.size () > 0)
            Storage.delFicheros (removed.elements ());
        if (added.size () > 0)
            Storage.addFicheros (added.elements ());
    }


    public static void updateDownloaded (File fd, String md5)
    {
        Fichero f;

        f = new Fichero (fd.getName (), md5, fd.length (), fd.lastModified ());
        f.setLocal (true);
        localFiles.put (f.getNombre (), f);
    }

}

