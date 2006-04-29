package atory.net;
/**
 * Netfolder - Clase encargada de enviar i/o recibir datos a través de la red
 * $Revision$
 */

import java.io.*
import java.net.*

/**
 *  Clase encargada de enviar i/o recibir datos a través de la red
 */
public class Netfolder
{
   static final int PORT = 3330;
   static final int INTENTOS = 3;

   /**
    * Función encargada de enviar datos a través de la red. Devuelve 0 sel
    * envio se realiza correctamente, en cambio, devuelve -1 si la llamada
    * no ha tenido éxito.
    *
    * @param destino Dirección destino.
    * @param data Información a enviar.
    */
   public static int send(InetAdress destino, String data)
   {
      int i = 0;
      boolean b = true;
      while(b)
      try
      {
         Socket dest = new Socket(destino , PORT);
         OutputStream output = dest.getOutputStream();
         DataOutStream flujo = new DataOutputStream(output);
         flujo.writeUTF(data);
         dest.close();
         b = false;
         i = 0;
      } 
      catch(Exception e)
      {
         if(++i== INTENTOS)
         {
            b = false;
            i = -1;
         }
      }
      return i;
   }

}
