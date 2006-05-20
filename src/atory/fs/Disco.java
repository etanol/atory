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
 * Clase para gestionar el acceso al directorio compartido.
 */
public class Disco {

    private static File      dirComp; 
    private static Hashtable localFiles;

    /**
     * Constructor (ignorado).
     */
    public Disco () {}

    /**
     * Inicializadora de la clase.
     */
    public static void init () throws Exception
    {
        int i;
        File lista[];
        Fichero f;

        dirComp    = new File (System.getProperty ("sharedir"));
        localFiles = new Hashtable ();

        // Escanea el directorio inicialmente
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
     * Fusiona la lista local con la lista de ficheros remotos.
     */
    public static void merge () throws Exception
    {
        Storage.addFicheros (localFiles.elements ());
    }

    /**
     * Escanea el directorio compartido en busca de cambios.
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

        // Buscar por elementos eliminados
        e = actual.elements ();
        while (e.hasMoreElements ()) {
            f = (Fichero) e.nextElement ();
            removed.addElement (f);
            localFiles.remove (f.getNombre ());
        }

        // Actualizar Storage, primero eliminados y luego añadidos
        Storage.delFicheros (removed.elements ());
        Storage.addFicheros (added.elements ());
    }

}

