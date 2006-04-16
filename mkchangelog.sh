#! /bin/sh

# Utilidad para invocar el script cvs2cl. La gracia de este script es que
# intenta encontrar el script en varios lugares distintos antes de fallar. Esté
# donde esté, es importante que el script tenga el permiso de ejecución
# activado.
#
# Con distribución basada en Debian:
#
# apt-get install cvs2cl
#
# Para descargar el script:
#
# http://www.red-bean.com/cvs2cl/
#
# $Revision$

rm -f ChangeLog*

cvs2cl_arguments='-S -P -b -T --show-dead'

jare="$IFS"
IFS=:

for dir in $PATH:$HOME:$HOME/bin:.; do

    # Probamos el nombre sin extensión (como en Debian)
    if [ -x $dir/cvs2cl ]; then
        # Restauramos el $IFS por si acaso
        IFS="$jare"
        $dir/cvs2cl $cvs2cl_arguments
        exit
    fi

    # Probamos con la extensión (descargado de la web)
    if [ -x $dir/cvs2cl.pl ]; then
        # Aquí hacemos exactamente lo mismo
        IFS="$jare"
        $dir/cvs2cl.pl $cvs2cl_arguments
        exit
    fi

done

