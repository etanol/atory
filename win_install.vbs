' win_install.vbs - Script instalador de Atory en plataformas Hasefroch
'
' $Revision$

' Pillar los objetos necesarios
Set shell = WScript.CreateObject ("WScript.Shell")
Set fs    = CreateObject ("Scripting.FileSystemObject")
desk      = shell.SpecialFolders ("Desktop")
mydocs    = shell.SpecialFolders ("MyDocuments")

' Crear el acceso directo
Set lnk = shell.CreateShortcut (desk & "\Atory.lnk")
lnk.TargetPath       = shell.CurrentDirectory & "\launch_atory.vbs"
lnk.WorkingDirectory = shell.CurrentDirectory
lnk.IconLocation     = "%SYSTEMROOT%\system32\shell32.dll,43"
lnk.Save

' Crear carpeta compartida, si no existe
If fs.FolderExists (mydocs & "\Atory_Shared") <> True Then
    fs.CreateFolder (mydocs & "\Atory_Shared")
End If

