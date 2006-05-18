/*
 * Atory.java - Clase principal.
 *
 * Esta es la clase que arranca todo el tinglado.
 *
 * $Revision$
 */
package atory;

public class Atory {

    /* 
     * La idea es que todos los hilos vayan igual. Ya que nuestras clases
     * principales son estáticas, lo que tenemos que hacer es envolverlas en
     * hilos.
     */
    class GUI extends Thread {
        public GUI () {}

        public void run ()
        {
            atory.gui.MainWindow.main (null);
        }
    }

    public static void main (String args[])
    {

    }

}

