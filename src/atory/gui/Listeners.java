/*
 * Listeners.java
 *
 * $Revision$
 */
package atory.gui;

import atory.*;
import atory.net.*;
import atory.xml.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

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
class ConexionesListener implements Listener {
    public void handleEvent (Event ev)
    {
        // La visibilidad de los campos en MainWindow es "package" así que
        // podemos utilizarlos aquí.
        Conexiones con = new Conexiones
        (MainWindow.shell, MainWindow.Nombres,
        MainWindow.IPs);
        con.open ();
    }
}

/* DESCONECTAR */
class DesconectarListener implements Listener {
    public void handleEvent (Event ev)
    {
       try {
         Storage.disconnect();
       } catch (Exception e) {
         MainWindow.error(e.getMessage());
       }
    }
}

/* TRANSMISIÓN SEGURA */
class TSegura1Listener implements Listener {
    public void handleEvent (Event ev)
    {
       if (MainWindow.menuTSeg.getSelection())
       {
          MainWindow.tsegItem.setSelection(true);
          MainWindow.tSegura = true;
       }
       else
       {
          MainWindow.tsegItem.setSelection(false);
          MainWindow.tSegura = false;
       }
       
    }
}

class TSegura2Listener implements Listener {
    public void handleEvent (Event ev)
    {       
       if (MainWindow.tsegItem.getSelection())
       {
          MainWindow.menuTSeg.setSelection(true);
          MainWindow.tSegura = true;
       }
       else
       {
          MainWindow.menuTSeg.setSelection(false);
          MainWindow.tSegura = false;
       }
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
           TableItem[] t_select = (MainWindow.tabla).getSelection();
           for(int i=0; i<t_select.length; i++)
           {
              atory.Storage.reqFichero (t_select[0].getText(0), MainWindow.tSegura);  
            }
        } catch (Exception e) {
           MainWindow.error (MainWindow.shell, e.getMessage() );
        }
    }
}
/* OCULTAR */
class OcultarListener implements Listener {

    public void handleEvent (Event e)
    {
                MainWindow.shell.setVisible (false);
		//System Tray
                Tray tray = MainWindow.display.getSystemTray();
                if(tray != null) 
                {
      	           final Image trayImage = new Image(MainWindow.display, (new ImageData(MainWindow.class.getResourceAsStream("images/ninjahiro.png"))).scaledTo(25,25));
                   final TrayItem trayItem = new TrayItem(tray, SWT.NONE);
                   trayItem.setImage(trayImage);
                   trayItem.setToolTipText("ATORY");
		   final Menu mTray = new Menu (MainWindow.shell, SWT.POP_UP);
		   final MenuItem salir = new MenuItem (mTray, SWT.PUSH);
		   final MenuItem abrir = new MenuItem (mTray, SWT.PUSH);
		   
		   //doble clic sobre el systray
 	           trayItem.addSelectionListener(new SelectionAdapter() {
                      public void widgetDefaultSelected(SelectionEvent e) {
	                 MainWindow.shell.setVisible(true);
	                 MainWindow.shell.setActive();
	                 MainWindow.shell.setFocus();
	                 MainWindow.shell.setMinimized(false);
			 trayImage.dispose();
			 trayItem.dispose();
			 abrir.dispose();
			 salir.dispose();
			 mTray.dispose();
	              }
	            });
		    //mini menu opcion abrir / salir
		    
		    salir.setText ("Salir");
		    salir.addListener(SWT.Selection, new Listener() {
                       public void handleEvent(Event e) {
			 trayImage.dispose();
			 trayItem.dispose();
			 abrir.dispose();
			 salir.dispose();
			 mTray.dispose();
       try {
          ParserXML.xmlEliminarHost (Netfolder.whoAmI());
          Netfolder.finish();
			 MainWindow.shell.dispose();
		   } catch (Exception ex) {
            MainWindow.error("Error: " + ex.getMessage());
         }
         }
		    });
		    
		    abrir.setText("Abrir");
		    abrir.addListener(SWT.Selection, new Listener() {
		       public void handleEvent(Event e) {
		         MainWindow.shell.setVisible(true);
	                 MainWindow.shell.setActive();
	                 MainWindow.shell.setFocus();
	                 MainWindow.shell.setMinimized(false);
			 trayImage.dispose();
			 trayItem.dispose();
			 abrir.dispose();
			 salir.dispose();
			 mTray.dispose();
		       }
		    });

		    trayItem.addListener(SWT.MenuDetect, new Listener() {
		       public void handleEvent(Event e) {
		          mTray.setVisible(true);
		       }
		    });
                 }
		e.doit = false;
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
        layoutv.fill = false;
        layoutv.justify = false;
        dialogoAD.setLayout(layoutv);
        Label txt = new Label (dialogoAD, SWT.CENTER);
        txt.setText("Another direcTORY\nV 1.0\nProjecte de Xarxes de Computadors\n");
        Button b = new Button(dialogoAD, SWT.PUSH | SWT.CENTER);
        b.setText("Aceptar");
	//TODO layout
        b.addListener (SWT.Selection, new Listener () {
            public void handleEvent (Event eb) {
                dialogoAD.dispose();
            }
        });
        dialogoAD.pack();
        dialogoAD.open();
    }
}


