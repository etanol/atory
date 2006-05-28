/*
 * Atory.java
 *
 * $Revision$
 */
package atory;

import atory.gui.MainWindow;
import atory.net.Netfolder;
import atory.xml.ParserXML;
import atory.fs.Disco;

/* 
 * Ya que nuestras clases principales son estáticas, lo que tenemos que hacer es
 * envolverlas en hilos.
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

/**
 * Clase principal. Esta clase es la que monta todo el tinglado. Se limita a
 * invocar a las inicializaciones y crear los hilos básicos necesarios.
 */
public class Atory {

    public static void main (String args[]) throws Exception
    {
        NET n = new NET ();

        Errlog.init ();
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

