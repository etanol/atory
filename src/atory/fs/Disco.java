/**
 * Clase Disco.
 * $Revision$
 * Implementa el acceso a disco del directorio compartido.
 *
 */

package atory.fs;
import atory.*;
import java.io.File;
import java.util.Vector;

public class Disco
{
   /**
    * Directorio Compartido.
    */
   private File dirComp; 
   
   /**
    * Constructora Disco.
    * @param path_dir Path del directorio compartido.
    */
   public Disco(String path_dir) throws Exception
   {
      dirComp = new File(path_dir);
      if(!dirComp.isDirectory())
         throw new Exception("Path no existe o no es un directorio");
   }

   
   /**
    * @return Devuelve un String con el path del directorio compartido.
    */
   public String getDirComp()
   {
      return dirComp.getAbsolutePath();
   }

   
   /**
    * @return Vector de tipo "Fichero" con los ficheros del directorio
    * compartido con los atributos nombre, md5 y tamano correspondiente
    * (atributo hosts no se rellena). 
    */
   public Vector ficherosDirComp() throws Exception 
   {
      Vector ficheros = new Vector(10,5);
      File vfiles[];
      int n;
      long l, fecha;
      Fichero f;
      String name, enc;
      MD5 md = new MD5();
      
     
      vfiles = dirComp.listFiles();
      n = vfiles.length;
      for(int i=0;i<n;i++)
      {
         if(vfiles[i].isFile() && vfiles[i].canRead())
         {
            name = vfiles[i].getName();
            enc = md.fromFile(vfiles[i]);
            l = vfiles[i].length();
            fecha = vfiles[i].lastModified();
            //Se añade aquí el host?
            //Si fuese asi, tendria que llamar al metodo whoAmI
            //con lo que esta clase se tendria que relacionar tambien
            //con Netfolder
            f = new Fichero(name,enc,l,fecha);
            ficheros.addElement(f);
         }
      }

      return ficheros;
   }

}
