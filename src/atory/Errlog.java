/*
 * Errlog.java
 *
 * $Revision$
 */
package atory;

import java.io.*;


/**
 * Clase para loggear errores en un fichero.
 */
public class Errlog {

    private static final String ERRLOG_FILE = "error_log.txt";

    private static FileOutputStream file;
    private static PrintWriter      logger;

    /**
     * Constructor (ignorado).
     */
    public Errlog () {}

    /**
     * Inicializador de la clase. Abre el fichero necesario para escribir los
     * mensajes de error.
     */
    public static void init ()
    {
        try {
            file   = new FileOutputStream (ERRLOG_FILE, false);
            logger = new PrintWriter (file, true);
        } catch (Exception ex) {
            System.err.println ("ERROR FATAL: No puedo crear '" + ERRLOG_FILE + "'");
            System.exit (0);
        }
    }

    /**
     * Imprime un mensaje sin salto de línea.
     *
     * @param s Mensaje a escribir en el fichero de log.
     */
    public static void print (String s)
    {
        logger.print (s);
    }

    /**
     * Imprime un mensaje con salto de línea.
     *
     * @param s Mensaje a escribir en el fichero de log.
     */
    public static void println (String s)
    {
        logger.println (s);
    }
}

