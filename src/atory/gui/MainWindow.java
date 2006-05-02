package atory.gui;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
//import org.eclipse.swt.graphics.*;

public class MainWindow {

								/*
									* @param args
									*/
								public static void main(String[] args) {

																final Display display = new Display ();
																final Shell shell = new Shell(display);
																shell.setText("ATORY");
																
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
																																List lista = new List (dialogo1, SWT.SINGLE);
																																//TODO: get de conexiones ya configuradas como array de strings
																																//para pasarselas al combo
																																lista.setItems (new String [] {"Atory1", "Atory2", "Atory3"});
																																Button b = new Button(dialogo1, SWT.PUSH);
																																b.setText("Conectar");
																																b.addListener (SWT.Selection, new Listener () {
																																								public void handleEvent (Event eb) {
																																																//TODO: enviar al FS la peticion de conexion del atory indicado (?)
																																																// ControlGUI.sendConexion(lista.getSelection()[0]);  <-- algo asi
																																																dialogo1.dispose();
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
																								public void handleEvent (Event e) {
																								}
																});
																item13.setText ("Con&figurar conexion\tCtrl+F");
																item13.setAccelerator (SWT.MOD1 + 'F');

																MenuItem item14 = new MenuItem (subArchivo,SWT.PUSH);
																item14.addListener (SWT.Selection, new Listener () {
																								public void handleEvent (Event e) {
																								}
																});
																item14.setText ("&Compartir &nuevo\tCtrl+N");
																item14.setAccelerator (SWT.MOD1 + 'N');

																MenuItem item15 = new MenuItem (subArchivo,SWT.PUSH);
																item15.addListener (SWT.Selection, new Listener () {
																								public void handleEvent (Event e) {
																								}
																});
																item15.setText ("&Leer archivo\tCtrl+L");
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
/*    //////TODO??
																RowLayout layoutv = new RowLayout(SWT.VERTICAL);
																layoutv.wrap = true;
																layoutv.fill = true;
																layoutv.justify = false;
																shell.setLayout(layoutv);
																
																//toolbar
																Image image = new Image (display, 16, 16);
																Color color = display.getSystemColor (SWT.COLOR_YELLOW);
																GC gc = new GC (image);
																gc.setBackground (color);
																gc.fillRectangle (image.getBounds ());
																gc.dispose ();
																ToolBar toolBar = new ToolBar (shell, SWT.FLAT | SWT.BORDER);
																ToolItem syncItem = new ToolItem (toolBar, SWT.PUSH);
																syncItem.setImage (image);
																toolBar.pack ();
*/
																//layout horizontal para los arboles
																RowLayout layouth = new RowLayout(SWT.HORIZONTAL);
																layouth.wrap = true;
																layouth.fill = false;
																layouth.justify = true;
																shell.setLayout(layouth);

																//arboles

																Tree tree = new Tree(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
																tree.setHeaderVisible(true);
																TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
																column1.setText("Ficheros compartidos");
																column1.setWidth(200);
																TreeColumn column2 = new TreeColumn(tree, SWT.CENTER);
																column2.setText("Propietario");
																column2.setWidth(100);
																TreeColumn column3 = new TreeColumn(tree, SWT.RIGHT);
																column3.setText("Estado");
																column3.setWidth(100);
																TreeItem one = new TreeItem (tree, 0);
																one.setText (new String[]{"Atory X", "c5", "OFF"});
																
																Tree tree2 = new Tree(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
																tree.setHeaderVisible(true);
																TreeItem iItem = new TreeItem (tree2, 0);
																iItem.setText ("Directorio local" );
																//fin
																shell.setSize (600, 400);
																//shell.pack();
																shell.open ();
																while (!shell.isDisposed ()) {
																								if (!display.readAndDispatch ()) display.sleep ();
																}
																display.dispose ();
								}

}
