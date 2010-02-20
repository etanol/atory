' launch_atory.vbs - Script de ejecucion para Hasefroch. Teniamos problemas con
'                    los accesos directos y las lineas de comandos demasiado
'                    largas.
'
' $Revision$

Set shell = WScript.CreateObject ("WScript.Shell")
mydocs    = shell.SpecialFolders ("MyDocuments")

shell.Run ("javaw -cp atory.jar;swt.jar;xpp3-1.1.3_7.jar -Djava.library.path=" _
           & ". -Dsharedir=" & chr(34) & mydocs & "\Atory_Shared\\" & chr(34) _
           & " -Drcfile=" & chr(34) & "%USERPROFILE%\atoryrc" & chr(34) _
           & " -Djavax.net.ssl.keyStore=keystore " _
           & "-Djavax.net.ssl.keyStorePassword=shishimaru " _
           & "-Djavax.net.ssl.trustStore=c-cacerts.jks " _
           & "-Djavax.net.ssl.trustStorePassword=shishimaru atory.Atory")

