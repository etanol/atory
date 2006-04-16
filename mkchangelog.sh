#! /bin/sh

# Utilidad para invocar el script cvs2cl. Con distribución basada en Debian:
#
# apt-get install cvs2cl
#
# Para descargar el script:
#
# http://www.red-bean.com/cvs2cl/
#
# $Revision$

rm -f ChangeLog*
cvs2cl -S -P -b -T --show-dead 

