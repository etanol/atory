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

    private static Vector listaNombres;
    private static Vector listaIPs;

    private Shell   ventana;
    private List    lstConexiones;
    private Text    txtNombre;     
    private Text    txtIP;
    private Button  btnGuardar;
    private Button  btnEliminar;
    private Button  btnConectar;

    /**
     * Constructor. 
     *
     * @param padre   Ventana padre sobre la que tenemos que aparecer.
     * @param nombres Vector de nombres de las
     * conexiones
     * @param ips Vector de ips de las
     * conexiones
     */
    public Conexiones (Shell padre, Vector
    nombres, Vector ips)
    {
        super (padre, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        listaNombres = nombres;
        listaIPs = ips;
    }

    /**
     * Mostrar el diálogo.
     */
    public void open ()
    {
        Group grpInfo;
        Label lConex, lNom, lIp;
        GridLayout gl;
        GridData gd;
        ventana = new Shell (this.getParent (), this.getStyle ());
        ventana.setText("Conexiones");
        
        gl = new GridLayout();
        gl.numColumns = 2;
        ventana.setLayout(gl);

        lConex = new Label(ventana, SWT.CENTER);
        lConex.setText("Conexiones");
        gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalSpan = 2;
        lConex.setLayoutData(gd);

        lstConexiones = new List(ventana, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
        //lstConexiones.add(new String("[Nueva Conexión]"));
         for(int i=0; i<listaNombres.size(); i++)
         {
            lstConexiones.add((String)(listaNombres.elementAt(i)));
         }
         lstConexiones.setSize(80,120);
         //TODO listener y layout

         grpInfo = new Group(ventana, SWT.NONE);
         grpInfo.setText("Datos de conexión");
         gl = new GridLayout();
         gl.numColumns = 2;
         grpInfo.setLayout(gl);
         gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
         gd.horizontalSpan = 2;
         grpInfo.setLayoutData(gd);

         lNom = new Label(grpInfo, SWT.NONE);
         lNom.setText("Nombre: ");
         txtNombre = new Text(grpInfo, SWT.SINGLE | SWT.BORDER);
         txtNombre.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         lIp = new Label(grpInfo, SWT.NONE);
         lIp.setText("Ip: ");
         txtIP = new Text(grpInfo, SWT.SINGLE | SWT.BORDER);
         txtIP.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         btnGuardar = new Button(ventana, SWT.PUSH);
         btnGuardar.setText("Guardar");
         //TODO layout
         btnGuardar.addListener(SWT.Selection, this);

         btnEliminar = new Button(ventana, SWT.PUSH);
         btnEliminar.setText("Eliminar");
         btnEliminar.addListener(SWT.Selection, this);
         
         btnConectar = new Button(ventana, SWT.PUSH);
         btnConectar.setText("Conectar");
         btnConectar.addListener(SWT.Selection, this);

        ventana.pack ();
        ventana.open ();
    }

    /**
     * Gestionar el evento del botón. 
     * @param ev El evento generado por SWT.
     */
    public void handleEvent (Event ev)
    {
        if((ev.widget).equals(btnGuardar))
        {
            String nombre, ip;

            nombre = txtNombre.getText ().trim ();
            ip     = txtIP.getText ().trim ();

            if ((nombre.length () == 0) || (ip.length () == 0)) 
            {
                MainWindow.error (ventana, "No deje ningún campo vacío" );
            }
            else if(listaNombres.contains(nombre))
            {
               int indx = listaNombres.indexOf(nombre);
               listaIPs.set(indx, ip);
            }
            else
            {
                listaNombres.addElement (nombre);
                listaIPs    .addElement (ip);
                lstConexiones.add(nombre);
            }

        }
        else if ((ev.widget).equals(btnEliminar))
        {
           int indx = lstConexiones.getSelectionIndex();
           if(indx==-1)
           {
                MainWindow.error (ventana, "Seleccione una conexión para eliminar" );
           }
           else
           {
              lstConexiones.remove(indx);
               listaNombres.removeElementAt(indx);
               listaIPs.removeElementAt(indx);
           }
        }
        else if((ev.widget).equals(btnConectar))
        {
            int i = lstConexiones.getSelectionIndex ();
            if (i == -1) 
            {
                MainWindow.error ("Por favor, seleccione una conexión");
            } 
            else 
            {
                try {
                    ParserXML.xmlNuevaConexion ((String)listaIPs.elementAt (i));
                } catch (Exception ex) {
                    MainWindow.error ("Excepción XML: "+ex.getMessage ());
                }
            ventana.dispose ();
            }

        }
    }
}

