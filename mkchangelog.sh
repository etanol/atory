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

PATH="$PATH:$HOME/bin:$HOME:."

bin=`which cvs2cl 2>/dev/null`
if [ -n "$bin" ]; then
    $bin $cvs2cl_arguments
    exit
fi

bin=`which cvs2cl.pl 2>/dev/null`
if [ -n "$bin" ]; then
    $bin $cvs2cl_arguments
    exit
fi

echo "ERROR: No cvs2cl found"

