#! /bin/sh

# linux_install.run - Instalador para Linux
#
# $Revision$

# Directorios a tratar
thisdir=`pwd`
sharedir="$HOME/Atory_Shared/"
rcfile="$HOME/.atoryrc"

# Crear el .desktop (acceso directo)
cat >$HOME/Desktop/atory.desktop <<EOD
[Desktop Entry]
Version=1.0
Encoding=UTF-8
Type=Application
Name=Atory
Path=$thisdir
Exec=java -cp atory.jar:swt.jar:xpp3-1.1.3_7.jar -Djava.library.path='.' -Dsharedir='$sharedir' -Drcfile='$rcfile' atory.Atory
Icon=$thisdir/shishimaru.png
Comment=Another direcTORY
EOD

# Crear directorio compartido, si no existe
test -d $sharedir || mkdir -p $sharedir

