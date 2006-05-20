/*
 * Atory.java - Clase principal.
 *
 * Esta es la clase que arranca todo el tinglado.
 *
 * $Revision$
 */
package atory;

import atory.gui.MainWindow;
import atory.net.Netfolder;
import atory.xml.ParserXML;
import atory.fs.Disco;

/* 
 * La idea es que todos los hilos vayan igual. Ya que nuestras clases
 * principales son estáticas, lo que tenemos que hacer es envolverlas en
 * hilos.
 */
class NET extends Thread {
    public NET () {}

    public void run ()
    {
        try {
            Netfolder.getXml ();
        } catch (Exception ex) {}
    }
}

public class Atory {

    public static void main (String args[]) throws Exception
    {
        NET n = new NET ();

        Storage.init ();
        Netfolder.init ();
        ParserXML.init ();
        Disco.init ();

        n.start();

        MainWindow.main (null);
        System.exit (0);
//        Thread.sleep (2000);
//        Disco.merge ();
    }

}

