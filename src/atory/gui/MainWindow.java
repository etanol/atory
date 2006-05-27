package atory.gui;

import atory.*;
import atory.xml.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import java.util.Vector;
import java.text.NumberFormat;

/**
 * Clase para mostrar la ventana principal y gestionar sus eventos. Comunicación
 * con el usuario.
 */
public class MainWindow {

    static Display display;
    static Shell   shell;
    static Table tabla;
    static Conexion con;
    static Vector Nombres; 
    static Vector IPs;     

    /**
     * Función principal. Utilizamos main con un doble propósito: poder arrancar
     * la interfaz directamente por separado para previsualizaciones y la
     * invocación normal junto con los demás módulos.
     *
     * @param args No se utiliza, únicamente se especifica para que el prototipo
     *             encaje.
     */
    public static void main(String[] args)
    {
        display = new Display ();
        shell   = new Shell(display);
        shell.setText("ATORY");
        Image ish = new Image (display, MainWindow.class.getResourceAsStream("images/ninjahiro.png"));
        shell.setImage(ish);
        Nombres = new Vector();
        IPs = new Vector();

        // Barra de menú, elementos principales.
        Menu menuBar     = new Menu (shell, SWT.BAR);
        Menu subArchivo  = new Menu (menuBar);
        Menu subAyuda    = new Menu (menuBar);
        MenuItem archivo = new MenuItem (menuBar, SWT.CASCADE);
        MenuItem ayuda   = new MenuItem (menuBar, SWT.CASCADE);

        archivo.setText ("&Atory");
        archivo.setMenu (subArchivo);
        ayuda  .setText ("A&yuda");
        ayuda  .setMenu (subAyuda);

        // Barra de menú, submenú Archivo

        // Conexiones
        MenuItem item = new MenuItem (subArchivo, SWT.PUSH);
        item.setText        ("C&onexiones...\tCtrl+O");
        item.setAccelerator (SWT.MOD1 + 'O');
        item.addListener    (SWT.Selection, new ConexionesListener ()); 

        // Desconectar
        item = new MenuItem (subArchivo, SWT.PUSH);
        item.setText        ("&Desconectar\tCtrl+D");
        item.setAccelerator (SWT.MOD1 + 'D');
        item.addListener    (SWT.Selection, new DesconectarListener ());

        /*/ Configurar conexión
        item = new MenuItem (subArchivo, SWT.PUSH);
        item.setText        ("Con&figurar conexión\tCtrl+F");
        item.setAccelerator (SWT.MOD1 + 'F');
        item.addListener    (SWT.Selection, new ConfigurarListener ());*/

        // Sincronizar
        item = new MenuItem (subArchivo, SWT.PUSH);
        item.setText        ("&Sincronizar\tCtrl+N");
        item.setAccelerator (SWT.MOD1 + 'N');
        item.addListener    (SWT.Selection, new SincronizarListener ());

        // Descargar
        item = new MenuItem (subArchivo, SWT.PUSH);
        item.setText        ("Descargar\tCtrl+L");
        item.setAccelerator (SWT.MOD1 + 'L');
        item.addListener    (SWT.Selection, new DescargarListener ());

        // Cerrar (Salir)
        item = new MenuItem (subArchivo, SWT.PUSH);
        item.setText        ("&Cerrar\tCtrl+C");
        item.setAccelerator (SWT.MOD1 + 'C');
        item.addListener    (SWT.Selection, new Listener () {
            public void handleEvent (Event e)
            {
                shell.setVisible (false);
            }
        });

        // Barra de menú, submenú Ayuda

        // Acerca de
        item = new MenuItem (subAyuda,SWT.PUSH);
        item.setText     ("Acerca de...");
        item.addListener (SWT.Selection, new AcercaDeListener ());

        // Coloca, finalmente, la barra de menú
        shell.setMenuBar (menuBar);

        // Prepara el layout para la ventana
        GridLayout layoutv = new GridLayout (1, true);
        layoutv.verticalSpacing = 3;
        shell.setLayout (layoutv);
        GridData gdata; // Ajustes para cada widget

        Image imaged = new Image (display, MainWindow.class.getResourceAsStream("images/stock_down.png"));
        Image images = new Image (display, MainWindow.class.getResourceAsStream("images/sync.png"));
        ToolBar toolBar = new ToolBar (shell, SWT.FLAT|SWT.BORDER);

        // La barra de herramientas NO se expande verticalmente
        gdata = new GridData (GridData.FILL_BOTH);
        gdata.grabExcessVerticalSpace = false;
        toolBar.setLayoutData (gdata);

        ToolItem downItem = new ToolItem (toolBar, SWT.PUSH);
        ToolItem syncItem = new ToolItem (toolBar, SWT.PUSH);
        downItem.setImage (imaged);
        syncItem.setImage (images);
        downItem.setToolTipText ("Descargar");
        syncItem.setToolTipText ("Sincronizar");
        downItem.addListener (SWT.Selection, new DescargarListener());
        syncItem.addListener (SWT.Selection, new SincronizarListener());
        toolBar.pack ();

        //tabla de ficheros
        tabla = new Table(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        tabla.setHeaderVisible(true);
        
        // El listado de los ficheros SÍ se expande verticalmente
        gdata = new GridData (GridData.FILL_BOTH);
        gdata.grabExcessVerticalSpace = true;
        tabla.setLayoutData (gdata);

        //columnas
        TableColumn column1 = new TableColumn(tabla, SWT.LEFT);
        column1.setText("Nombre");
        column1.setWidth(150);
        column1.setResizable(true);
        TableColumn column3 = new TableColumn(tabla, SWT.CENTER);
        column3.setText("Ubicación");
        column3.setWidth(80);
        column3.setResizable(false);
        TableColumn column2 = new TableColumn(tabla, SWT.CENTER);
        column2.setText("Tamaño");
        column2.setWidth(100);
        column2.setResizable(false);
        tabla.pack();
        
      //System Tray
      Tray tray = display.getSystemTray();
      if(tray != null) {
      Image trayImage = new Image(display, (new
      ImageData(MainWindow.class.getResourceAsStream("images/ninjahiro.png"))).scaledTo(25,25));
         TrayItem trayItem = new TrayItem(tray, SWT.NONE);
         trayItem.setImage(trayImage);
      }

      visualizarLista (new Vector ());
      try {
          atory.fs.Disco.merge ();
      } catch (Exception ex) {}

      //fin
      shell.setSize (350, 420);
      //shell.pack();
      shell.open ();
      /*while (true)
      {
         if (!display.readAndDispatch ()) display.sleep ();
      }*/
      
      while (!shell.isDisposed ()) {
         if (!display.readAndDispatch ()) display.sleep ();
      }
      display.dispose ();
   }
   
   /**
    * Elimina todos los items de la tabla.
    */
   public static void eliminarTodos()
   {
      display.asyncExec (new Runnable () {
          public void run ()
          {
            tabla.removeAll();
          }
      });
   }
   
   /**
    * Comunica a la interfaz que se tiene que
    * visualizar la lista. Esta función llama a
    * anyadirFichero() por cada uno de los
    * ficheros de la lista.
    * @param lista Lista de ficheros a mostrar
    */
   public static void visualizarLista(final Vector lista)  //lista de ficheros
   {
      final Vector lst = lista;
      display.asyncExec (new Runnable () {
          public void run ()
          {
              for(int i=0;i<lst.size();i++) {
                  Fichero f = (Fichero) lst.elementAt(i);
                  anyadirFichero(f);
              }
          }
      });
   }

   /**
    * Añade un fichero a la lista gráfica. Recoge
    * los datos del fichero de entrada para
    * mostrar los datos correspondientes.
    * @param fich Fichero a insertar
    */
   public static void anyadirFichero(Fichero fich)
   {
       final Fichero f = fich;
       display.asyncExec (new Runnable() {
           public void run ()
           {
               String nombre, nimag, tam, ubi;
               Image icon;
               nombre = f.getNombre();
               String[] ext = nombre.split("[.]");
               if(ext.length==0) {
                   icon = new Image (display, (new ImageData(MainWindow.class.getResourceAsStream("images/generico.png"))).scaledTo(25,25) );
               } else {
                   nimag="images/" + ext[(ext.length)-1] + ".png";
                   try{
                       icon = new Image (display, (new ImageData(MainWindow.class.getResourceAsStream(nimag))).scaledTo(20,20) );
                   } catch (Exception e) {
                       icon = new Image (display, (new ImageData(MainWindow.class.getResourceAsStream("images/generico.png"))).scaledTo(25,25) );
                   }
               }
               NumberFormat nf = NumberFormat.getInstance();
               nf.setMaximumFractionDigits(3);
               //que escala de bytes ponemos?
               if (f.getTamano()>1024*1024)
                  tam = nf.format((double)(f.getTamano()) / (1024*1024)) + " MB";
                  //tam = String.valueOf((double)(f.getTamano()) / (1024*1024)) + " MB";
               else if (f.getTamano()>1024)
                  tam = nf.format((double)(f.getTamano()) / 1024) + " KB";
               else 
                  tam = String.valueOf((double)(f.getTamano())) + " B";
                  
               //crear el item
               TableItem item = new TableItem (tabla, SWT.NONE);
               if(f.isLocal()) 
                  ubi = "Local";
               else 
                  ubi = "Remoto";
               item.setImage(icon);
               item.setText (new String[]{ nombre, ubi, tam });
           }
       });
   }

   /**
    * Elimina un fichero de la lista gráfica.
    * Busca el fichero según su nombre y lo
    * elimina de la tabla.
    * @param fich Fichero a eliminar.
    */
   public static void eliminarFichero(Fichero fich)
   {
       final Fichero f = fich;
       display.asyncExec (new Runnable () {
           public void run ()
           {
               TableItem[] ti = tabla.getItems();
               for(int i=0; i < ti.length; i++) 
               {
                   if (ti[i].getText(0).equals(f.getNombre())) 
                   {
                       tabla.remove(i);
                       break;
                   }   
               }
           }
       });
   }

   /**
    * Muestra un mensaje emergente de error. La ventana es modal y, por tanto,
    * la ejecución se detiene hasta que el usuario ha presionado el botón de
    * acpetar.
    *
    * @param padre Ventana padre sobre la que se mostrará esta ventana
    *              emergente.
    * @param msg   Mensaje de error que se mostrará en la ventana emergente.
    */
   public static void error (Shell padre, String msg)
   {
       MessageBox errMsg;
       
       errMsg = new MessageBox (padre, SWT.ICON_ERROR | SWT.OK);
       errMsg.setMessage (msg);
       errMsg.open       ();
   }

   /**
    * Muestra un mensaje emergente de error. En este caso la ventana padre es la
    * ventana principal.
    *
    * @param msg El mensaje de error a visualizar en la ventana emergente.
    */
   public static void error (String msg)
   {
       error (shell, msg);
   }

}

