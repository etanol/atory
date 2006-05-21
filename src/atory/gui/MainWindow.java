package atory.gui;

import atory.*;
import atory.xml.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import java.util.Vector;

/**
 * Clase para mostrar la ventana principal y gestionar sus eventos. Comunicación
 * con el usuario.
 */
public class MainWindow {

    static Display display;
    static Shell   shell;
    static Tree tree;
    static Conexion con;
    static TreeItem papi;
    static Vector conexion;

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

        conexion = new Vector();

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

        // Conectar
        MenuItem item = new MenuItem (subArchivo, SWT.PUSH);
        item.setText        ("C&onectar\tCtrl+O");
        item.setAccelerator (SWT.MOD1 + 'O');
        item.addListener    (SWT.Selection, new ConectarListener ()); 

        // Desconectar
        item = new MenuItem (subArchivo, SWT.PUSH);
        item.setText        ("&Desconectar\tCtrl+D");
        item.setAccelerator (SWT.MOD1 + 'D');
        item.addListener    (SWT.Selection, new DesconectarListener ());

        // Configurar conexión
        item = new MenuItem (subArchivo, SWT.PUSH);
        item.setText        ("Con&figurar conexión\tCtrl+F");
        item.setAccelerator (SWT.MOD1 + 'F');
        item.addListener    (SWT.Selection, new ConfigurarListener ());

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

        shell.setMenuBar (menuBar);
      //////TODO??
      RowLayout layoutv = new RowLayout(SWT.VERTICAL);
      layoutv.wrap = true;
      layoutv.fill = true;
      layoutv.justify = true;
      shell.setLayout(layoutv);

      //toolbar

      Image imaged = new Image (display, MainWindow.class.getResourceAsStream("images/stock_down.png"));
      Image images = new Image (display, MainWindow.class.getResourceAsStream("images/sync.png"));
      ToolBar toolBar = new ToolBar (shell, SWT.FLAT|SWT.BORDER);
      toolBar.setLayoutData(new RowData(260, 30));
      ToolItem downItem = new ToolItem (toolBar, SWT.PUSH);
      ToolItem syncItem = new ToolItem (toolBar, SWT.PUSH);
      downItem.setImage (imaged);
      syncItem.setImage (images);
      downItem.setToolTipText ("Descargar");
      syncItem.setToolTipText ("Sincronizar");
      downItem.addListener (SWT.Selection, new DescargarListener());
      syncItem.addListener (SWT.Selection, new SincronizarListener());
      toolBar.pack ();

      //arboles
      tree = new Tree(shell, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
      tree.setHeaderVisible(true);
      tree.setLayoutData(new RowData(260, 300));
      TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
      column1.setText("Nombre");
      column1.setWidth(120);
      column1.setResizable(true);
      TreeColumn column2 = new TreeColumn(tree, SWT.CENTER);
      column2.setText("Ubicación");
      column2.setWidth(80);
      column2.setResizable(true);
      TreeColumn column3 = new TreeColumn(tree, SWT.RIGHT);
      column3.setText("Tamaño");
      column3.setWidth(60);
      column3.setResizable(true);

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
      //shell.setSize (290, 420);
      shell.pack();
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

   public static void visualizarLista(final Vector lista)  //lista de ficheros
   {
      final Vector lst = lista;
      display.asyncExec (new Runnable () {
          public void run ()
          {
              papi =  new TreeItem (tree, SWT.NONE);
              papi.setText (new String[]{(con == null ? "" : con.nombre),"",""});
              for(int i=0;i<lst.size();i++) {
                  Fichero f = (Fichero) lst.elementAt(i);
                  anyadirFichero(f);
              }
          }
      });
   }

   public static void anyadirFichero(Fichero fich)
   {
       final Fichero f = fich;
       display.asyncExec (new Runnable() {
           public void run ()
           {
               String ubi, nombre, nimag;
               Image icon;
               if(f.isLocal()) ubi="Local";
               else ubi="Remoto";
               nombre = f.getNombre();
               String[] ext = nombre.split("[.]");
               if(ext.length==0) {
                   icon = new Image (display, (new ImageData(MainWindow.class.getResourceAsStream("images/generico.png"))).scaledTo(20,20) );
               } else {
                   nimag="images/" + ext[(ext.length)-1] + ".png";
                   try{
                       icon = new Image (display, (new ImageData(MainWindow.class.getResourceAsStream(nimag))).scaledTo(20,20) );
                   } catch (Exception e) {
                       icon = new Image (display, (new ImageData(MainWindow.class.getResourceAsStream("images/generico.png"))).scaledTo(20,20) );
                   }
               }
               TreeItem hijo = new TreeItem (papi, SWT.NONE);
               hijo.setImage(icon);
               hijo.setText (new String[]{ nombre, ubi, String.valueOf(f.getTamano())+" B"});
           }
       });
   }

   public void eliminarFichero(Fichero fich)
   {
       final Fichero f = fich;
       display.asyncExec (new Runnable () {
           public void run ()
           {
               TreeItem[] ti=papi.getItems();
               for(int i=0; i < ti.length; i++) {
                   if (ti[i].getText() == f.getNombre()) {
                       (ti[i].getItem(i)).dispose();
                       break;
                   }   
               }
       //TODO:tratar si no existe fichero a eliminar??
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

