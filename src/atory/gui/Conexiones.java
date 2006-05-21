/*
 * Conexiones.java
 *
 * $Revision$
 */
package atory.gui;

import atory.xml.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import java.util.Vector;

/**
 * Diálogo para gestionar las conexiones. Este diálogo se puede presentar en dos
 * formatos: edición de conexiones y lista de conexiones.
 */
class Conexiones extends Dialog implements Listener {

    private static Vector listaNombres = null;
    private static Vector listaIPs     = null;

    private boolean modo_edicion;
    private Shell   ventana;
    private Button  btnAceptar;
    private List    lstConexiones; // Sólo se usa en modo lista
    private Text    txtNombre;     // Sólo se usan en modo edición
    private Text    txtIP;

    /**
     * Constructor. Desde aquí seleccionamos el modo de visualización de este
     * cuadro de diálogo en funzión del parámetro booleano suministrado. Es
     * necesario indicar cuál será la ventana padre pues nosotros siempre
     * aparecermos de forma modal.
     *
     * @param padre   Ventana padre sobre la que tenemos que aparecer.
     * @param edicion Selecciona el modo edición si está a true, modo lista en
     *                caso contrario.
     */
    public Conexiones (Shell padre, boolean edicion)
    {
        super (padre, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

        // Si no hay lista de conexiones forzamos el modo edición
        if (listaNombres == null)
            edicion = true;
        modo_edicion = edicion;
    }

    /**
     * Mostrar el diálogo.
     */
    public void open ()
    {
        ventana = new Shell (this.getParent (), this.getStyle ());

        // La ventana se ajusta igual en cualquiera de los casos (¿SEGURO?)
        RowLayout layoutv = new RowLayout(SWT.VERTICAL);
        layoutv.wrap      = true;
        layoutv.fill      = true;
        layoutv.justify   = false;
        ventana.setLayout (layoutv);

        if (modo_edicion) {
            // Modo edición
            // TODO: ¡¡Necesita mejorar!!
            if (listaNombres == null) {
                listaNombres = new Vector ();
                listaIPs     = new Vector ();
            }

            ventana.setText ("Configurar conexión");

            Label lbl = new Label (ventana, SWT.LEFT);
            lbl.setText ("Introduzca un nombre para la conexión:");
            txtNombre = new Text (ventana, SWT.SINGLE);

            lbl = new Label (ventana, SWT.LEFT); // El original era SWT.SINGLE
            lbl.setText ("Introduzca la dirección IP");
            txtIP = new Text (ventana, SWT.SINGLE);

            btnAceptar = new Button (ventana, SWT.PUSH);
            btnAceptar.setText     ("Aceptar");
            btnAceptar.addListener (SWT.Selection, this);

        } else {
            // Modo lista
            this.setText ("Conectar");

            Label lbl = new Label (ventana, SWT.LEFT);
            lbl.setText ("Elija el ATORY al que desea conectarse:");

            lstConexiones = new List (ventana, SWT.SINGLE);
            lstConexiones.setItems ((String[]) listaNombres.toArray (new String[listaNombres.size()]));

            btnAceptar = new Button (ventana, SWT.PUSH);
            btnAceptar.setText     ("Conectar");
            btnAceptar.addListener (SWT.Selection, this);
        }

        ventana.pack ();
        ventana.open ();
    }

    /**
     * Gestionar el evento del botón. Dependiendo del modo en el que estábamos
     * (si se pulsó "Aceptar" o "Conectar") seleccionaremos un comportamiento u
     * otro.
     *
     * @param ev El evento generado por SWT.
     */
    public void handleEvent (Event ev)
    {
        if (modo_edicion) {
            // Modo edición
            String nombre, ip;

            nombre = txtNombre.getText ().trim ();
            ip     = txtIP.getText ().trim ();

            if ((nombre.length () == 0) || (ip.length () == 0)) {
                MainWindow.error (ventana, "¡No deje ningún campo vacío" );
            } else {
                listaNombres.addElement (nombre);
                listaIPs    .addElement (ip);
                ventana.dispose ();
            }

        } else {
            // Modo lista
            int i;

            i = lstConexiones.getSelectionIndex ();
            if (i == -1) {
                MainWindow.error ("Error inesperado: índice fuera de rango");
            } else {
                try {
                    ParserXML.xmlNuevaConexion ((String)listaIPs.elementAt (i));
                } catch (Exception ex) {
                    MainWindow.error ("Excepción XML: "+ex.getMessage ());
                }
            }
            ventana.dispose ();
        }
    }
}

