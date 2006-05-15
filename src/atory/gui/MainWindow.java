package atory.gui;

import atory.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import java.util.Vector;

/**
 *Clase para mostrar la ventana principal y gestionar sus eventos
 */
public class MainWindow {

   static Tree tree;
   static Conexion con;
   static TreeItem papi;
   static Display display;
   
   /**
    * @param args
    */
   public static void main(String[] args) {

      display = new Display ();
      final Shell shell = new Shell(display);
      shell.setText("ATORY");
      //final ControlGUI control = new ControlGUI(this);

      final Vector conexion=new Vector();
      //menu
      Menu menuBar = new Menu (shell, SWT.BAR);
      MenuItem archivo = new MenuItem (menuBar, SWT.CASCADE);
      archivo.setText ("&Atory");
      MenuItem ayuda = new MenuItem (menuBar, SWT.CASCADE);
      ayuda.setText ("A&yuda");
      Menu subArchivo = new Menu (menuBar);
      archivo.setMenu (subArchivo);
      Menu subAyuda = new Menu (menuBar);
      ayuda.setMenu (subAyuda);

      MenuItem item11 = new MenuItem (subArchivo,SWT.PUSH);
      item11.addListener (SWT.Selection, new Listener () {
         public void handleEvent (Event e) {
            final Shell dialogo1 = new Shell (shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
            dialogo1.setText("Conectar");
            RowLayout layoutv = new RowLayout(SWT.VERTICAL);
            layoutv.wrap = true;
            layoutv.fill = true;
            layoutv.justify = false;
            dialogo1.setLayout(layoutv);
            Label txt = new Label (dialogo1, SWT.LEFT);
            txt.setText("Elija el ATORY al que desea conectarse:");
            final List lista = new List (dialogo1, SWT.SINGLE);
            //YETDO: get de conexiones ya configuradas como array de strings
            //para pasarselas a la lista
            String[] conex;
            Vector v=new Vector();
            for(int i=0;i<conexion.size();i++)
         v.addElement(((Conexion)(conexion.get(i))).nombre);
      conex=(String[])v.toArray(new String[v.size()]);
      lista.setItems (conex);
      Button b = new Button(dialogo1, SWT.PUSH);
      b.setText("Conectar");
      b.addListener (SWT.Selection, new Listener () {
         public void handleEvent (Event eb) {
            //TODO: enviar al FS la peticion de conexion del atory indicado (?)
            // ControlGUI.sendConexion(lista.getSelection()[0]);  <-- algo asi
            con = (Conexion) (conexion.elementAt(lista.getSelectionIndex()));
            //Storage.reqHosts(con.nombre);
            Vector fichi=new Vector();
            fichi.addElement(new Fichero("dani.java","1234567abcd",(long)567.8));
            fichi.addElement(new Fichero("c5.hjk","8765432abcd",(long)99));
            visualizarLista(fichi);
            dialogo1.dispose();
            //TODO:entretener al usuario mientras se conecta??
         }
      });
      dialogo1.pack();
      dialogo1.open();
         }
      });
      item11.setText ("C&onectar\tCtrl+O");
      item11.setAccelerator (SWT.MOD1 + 'O');

      MenuItem item12 = new MenuItem (subArchivo,SWT.PUSH);
      item12.addListener (SWT.Selection, new Listener () {
         public void handleEvent (Event e) {
            //TODO: enviar peticion de desconexion
         }
      });
      item12.setText ("&Desconectar\tCtrl+D");
      item12.setAccelerator (SWT.MOD1 + 'D');

      MenuItem item13 = new MenuItem (subArchivo,SWT.PUSH);
      item13.addListener (SWT.Selection, new Listener () {
         public void handleEvent (Event e) 
      {
         final Shell dialogo3 = new Shell (shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
         dialogo3.setText("Configurar conexion");
         RowLayout layoutv = new RowLayout(SWT.VERTICAL);
         layoutv.wrap = true;
         layoutv.fill = true;
         layoutv.justify = false;
         dialogo3.setLayout(layoutv);
         Label nombreLabel = new Label (dialogo3, SWT.LEFT);
         nombreLabel.setText("Introduzca un nombre para la conexión:");
         final Text nombre = new Text (dialogo3, SWT.SINGLE);
         Label ipLabel = new Label (dialogo3, SWT.SINGLE);
         ipLabel.setText("Introduzca la dirección IP:");
         final Text ip = new Text (dialogo3, SWT.SINGLE);
         Button b = new Button(dialogo3, SWT.PUSH);
         b.setText("Aceptar");
         b.addListener (SWT.Selection, new Listener () {
            public void handleEvent (Event eb) {
               //TODO: tratar excepcion IP mal formada??
               String n = nombre.getText();
               String p = ip.getText();
               if(n.equals("") || p.equals(""))
         {
            System.out.println("que es un errorrrrrrrrr!!");
            final Shell error = new Shell (dialogo3, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
            RowLayout lay = new RowLayout(SWT.VERTICAL);
            lay.wrap = true;
            lay.fill = true;
            lay.justify = false;
            error.setLayout(lay);
            (new Label(error,SWT.SINGLE)).setText("No deje ningún campo vacío");
            Button bb=new Button(error, SWT.PUSH);
            bb.setText("Aceptar");
            bb.addListener (SWT.Selection, new Listener () {
               public void handleEvent (Event ev) {
                  error.dispose();
               }
            });
            //MessageDialog ups = new MessageDialog (dialogo3,new String("Atención"), null, 
            //   new String("No deje ningún campo vacío"),MessageDialog.WARNING,null,0);
            error.pack();
            error.open();
         }
               else
               {
                  //Conexion c=new Conexion(n,p);
                  Conexion c5=new Conexion();
                  c5.nombre=n;
                  c5.IP=p;
                  conexion.addElement(c5);
                  dialogo3.dispose();
               }
            }
         });
         dialogo3.pack();
         dialogo3.open();
      }
      });
      item13.setText ("Con&figurar conexion\tCtrl+F");
      item13.setAccelerator (SWT.MOD1 + 'F');

      MenuItem item14 = new MenuItem (subArchivo,SWT.PUSH);
      item14.addListener (SWT.Selection, new Listener () {
         public void handleEvent (Event e) {
         }
      });
      item14.setText ("&Sincronizar\tCtrl+N");
      item14.setAccelerator (SWT.MOD1 + 'N');

      MenuItem item15 = new MenuItem (subArchivo,SWT.PUSH);
      item15.addListener (SWT.Selection, new Listener () {
         public void handleEvent (Event e) {
         }
      });
      item15.setText ("Descargar\tCtrl+L");
      item15.setAccelerator (SWT.MOD1 + 'L');

      MenuItem item16 = new MenuItem (subArchivo,SWT.PUSH);
      item16.addListener (SWT.Selection, new Listener () {
         public void handleEvent (Event e) {
            display.dispose ();    }
      });
      item16.setText ("&Cerrar\tCtrl+C");
      item16.setAccelerator (SWT.MOD1 + 'C');

      MenuItem item21 = new MenuItem (subAyuda,SWT.PUSH);
      item21.addListener (SWT.Selection, new Listener () {
         public void handleEvent (Event e){
            final Shell dialogoAD = new Shell (shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
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
      });
      item21.setText ("Acerca de...");

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
      toolBar.pack ();

      //arboles
      tree = new Tree(shell, SWT.V_SCROLL | SWT.H_SCROLL);
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
      //TreeItem one = new TreeItem (tree, 0);
      //one.setText (new String[]{"Atory X", "Local", "5 K"});

      //System Tray
      Image trayImage = new Image(display, (new ImageData(MainWindow.class.getResourceAsStream("images/ninjahiro.png"))).scaledTo(30,25));
      Tray tray = display.getSystemTray();
      if(tray != null) {
	   TrayItem trayItem = new TrayItem(tray, SWT.NONE);
	   trayItem.setImage(trayImage);
      }
      
      //fin
      //shell.setSize (290, 420);
      shell.pack();
      shell.open ();
      while (!shell.isDisposed ()) {
         if (!display.readAndDispatch ()) display.sleep ();
      }
      display.dispose ();
   }
   
   public static void visualizarLista(Vector lista)  //lista de ficheros
   {
      papi =  new TreeItem (tree, SWT.NONE);
      papi.setText (new String[]{con.nombre,"",""});
      for(int i=0;i<lista.size();i++)
      {
         Fichero f = (Fichero) lista.elementAt(i);
        anyadirFichero(f);
      }
   }

   public static void anyadirFichero(Fichero f)
   {
      String ubi, nombre, nimag;
         Image icon;
         if(f.isLocal()) ubi="Local";
         else ubi="Remoto";
         nombre = f.getNombre();
         String[] ext = nombre.split("[.]");
         if(ext.length==0)
         {
            icon = new Image (display, (new ImageData(MainWindow.class.getResourceAsStream("images/generico.png"))).scaledTo(20,20) );
         }
         else
         {
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

   public void eliminarFichero(Fichero f)
   {
      TreeItem[] ti=papi.getItems();
      for(int i=0; i < ti.length; i++)
      {
         if (ti[i].getText() == f.getNombre())
         {
            (ti[i].getItem(i)).dispose();
            break;
         }   
      }
      //TODO:tratar si no existe fichero a eliminar??
   }
}

