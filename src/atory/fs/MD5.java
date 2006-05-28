/*
 * MD5.java
 *
 * $Revision$
 */
package atory.fs;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Funciones de utilidad para calcular hashes MD5. Todas los métodos son
 * estáticos para no tener que acarrear referencias a una instancia.
 */
public class MD5 {

    // Constantes
    private static final int  BUFSIZE = 4096;
    private static final char HEX[]   = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    // Globales privadas
    private static MessageDigest md = null;
    private static byte buffer[];

    /**
     * Computa el hash MD5 de un fichero. El fichero debe estar disponible dado
     * que es necesario leer su contenido.
     *
     * @param filename   Nombre del fichero a procesar. Aplican las mismas
     *                   reglas que en el constructor de FileInputStream
     * @return           Representación hexadecimal de los 128 bits (16 bytes)
     *                   que componen el hash MD5.
     * @throws Exception En caso de error de cálculo o de entrada salida.
     */
    public static String fromFile (String filename) throws Exception
    {
        FileInputStream fd;

        fd = new FileInputStream (filename);
        return computeDigest (fd);
    }

    /**
     * Computa el hash MD5 de un fichero. El fichero debe estar disponible dado
     * que es necesario leer su contenido.
     *
     * @param file       Representación abstracta de un fichero. Aplican las
     *                   mismas reglas que en el constructor de FileInputStream
     * @return           Representación hexadecimal de los 128 bits (16 bytes)
     *                   que componen el hash MD5.
     * @throws Exception En caso de error de cálculo o de entrada salida.
     */
    public static String fromFile (File file) throws Exception
    {
        FileInputStream fd;

        fd = new FileInputStream (file);
        return computeDigest (fd);
    }

    /**
     * Computa el digest. Esta es la función que realmente hace todo el trabajo.
     *
     * @param fd         Stream a través del cual se puede leer el contenido de
     *                   un fichero.
     * @return           Representación hexadecimal de los 128 bits (16 bytes)
     *                   que componen el hash MD5.
     * @throws Exception En caso de error de cálculo o de entrada salida.
     */
    private static String computeDigest (FileInputStream fd) throws Exception
    {
        int  i;
        byte digest[];
        StringBuffer sb;

        // Inicializar, si hace falta
        if (md == null) {
            md     = MessageDigest.getInstance ("MD5");
            buffer = new byte[BUFSIZE];
        }

        // Computar el hash
        md.reset ();
        i = fd.read (buffer);
        while (i > 0) {
            md.update (buffer, 0, i);
            i = fd.read (buffer);
        }
        digest = md.digest ();

        // Codificar a hexadecimal en un String
        sb = new StringBuffer (32);
        for (i = 0; i < 16; i++) {
            sb.append (HEX[(digest[i] >> 4) & 0x0F]);
            sb.append (HEX[digest[i] & 0x0F]);
        }
        return sb.toString();
    }

}

