#
# Makefile - Directorio raíz.
#
# $Revision$
#
Root    := .
Subdirs := atory

include $(Root)/rules.make

jar:
	@find . -name \*.class | xargs jar cvf atory.jar

.PHONY: dclean help
dclean: clean
	@-rm -fv abalone.jar; 

help:
	@echo 'Reglas genéricas:';\
	echo '   files    Compila los ficheros fuente del directorio actual.';\
	echo '   subdirs  Compila los ficheros fuente de los subdirectorios definidos';\
	echo '   clean    Limpia recursivamente los binarios generados.';\
	echo '';\
	echo 'Reglas para el directorio raíz:';\
	echo '   jar      Crea un fichero JAR que contiene todo el programa.';\
	echo '   dclean   Limpia el JAR generado (implica "clean")';\
	echo '   help     Esta ayuda.';\
	echo '';\
	echo 'La acción por defecto es "files". Si el Makefile define subdirectorios,';\
	echo 'también se procesarán recursivamente.'

