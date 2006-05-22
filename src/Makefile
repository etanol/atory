#
# Makefile - Directorio raíz.
#
# $Revision$
#
Root    := .
Subdirs := atory

include $(Root)/rules.make

package: linux_package win_package

win_package:
	@./pack hase

linux_package:
	@./pack linux

.PHONY: pclean help
pclean:
	@-rm -rfv linux_package win_package

help:
	@echo 'Reglas genéricas:';\
	echo '   files    Compila los ficheros fuente del directorio actual.';\
	echo '   subdirs  Compila los ficheros fuente de los subdirectorios definidos';\
	echo '   clean    Limpia recursivamente los binarios generados.';\
	echo '';\
	echo 'Reglas para el directorio raíz:';\
	echo '   package  Crea los paquetes con los binarios para su distribución.';\
	echo '   pclean   Limpia los paquetes.';\
	echo '   help     Esta ayuda.';\
	echo '';\
	echo 'La acción por defecto es "files". Si el Makefile define subdirectorios,';\
	echo 'también se procesarán recursivamente.'

