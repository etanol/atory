/*
 * Listeners.java
 *
 * $Revision$
 */
package atory.gui;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;

/*
 * Aquí haremos las implementaciones triviales de los listeners que tratarán los
 * eventos importantes de la ventana principal. Así podemos asociarlos a más de
 * un evento con el mínimo esfuerzo.
 *
 * La principal utilidad es poder tener una toolbar que llame a las mismas
 * funciones de los menús, ejecutando exactamente el mismo código.
 *
 * IMPORTANTE: Las clases aquí definidas NO deben declararse como públicas pues
 * sólo las utilizaremos dentro de este package. Además podremos definirlas
 * todas en este mismo fichero fuente.
 */

/* CONECTAR */
class ConectarListener implements Listener {
    public void handleEvent (Event ev)
    {
        // La visibilidad de los campos en MainWindow es "package" así que
        // podemos utilizarlos aquí.
        Conexiones con = new Conexiones (MainWindow.shell, false);
        con.open ();
    }
}

/* DESCONECTAR */
class DesconectarListener implements Listener {
    public void handleEvent (Event ev)
    {
        // TODO: enviar petición de desconexión
    }
}

/* CONFIGURAR CONEXIÓN */
class ConfigurarListener implements Listener {
    public void handleEvent (Event ev)
    {
        Conexiones con = new Conexiones (MainWindow.shell, true);
        con.open ();
    }
}

/* SINCRONIZAR */
class SincronizarListener implements Listener {
    public void handleEvent (Event ev)
    {
       try {
          atory.fs.Disco.sync();
       } catch (Exception e) {
         MainWindow.error (MainWindow.shell, "Error al sincronizar" );
       }
    }
}

/* DESCARGAR */
class DescargarListener implements Listener {
    public void handleEvent (Event ev)
    {
        try {
           TreeItem[] t_select = (MainWindow.tree).getSelection();
           atory.Storage.reqFichero (t_select[0].getText(0)); 
        } catch (Exception e) {
           MainWindow.error (MainWindow.shell, e.getMessage() );
        }
    }
}

/* ACERCA DE */
class AcercaDeListener implements Listener {
    /*
     * En realidad, esta sólo está aquí por comodidad. Así no se ensucia tanto
     * el código de MainWindow
     */
    public void handleEvent (Event ev)
    {
        final Shell dialogoAD = new Shell (MainWindow.shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialogoAD.setText("Acerca de ATORY");
        RowLayout layoutv = new RowLayout(SWT.VERTICAL);
        layoutv.wrap = true;
        layoutv.fill = true;
        layoutv.justify = false;
        dialogoAD.setLayout(layoutv);
        Label txt = new Label (dialogoAD, SWT.LEFT);
        txt.setText("This program is free software, YEAH!\nDevelopers: \nDC, XG, CG, IJ, IM");
        Button b = new Button(dialogoAD, SWT.PUSH);
        b.setText("I will pay your fees");
        b.addListener (SWT.Selection, new Listener () {
            public void handleEvent (Event eb) {
                //TODO: enviar al FS la peticion de conexion del atory indicado (?)
                // ControlGUI.sendConexion(lista.getSelection()[0]);  <-- algo asi
                dialogoAD.dispose();
            }
        });
        dialogoAD.pack();
        dialogoAD.open();
    }
}


