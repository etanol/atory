#
# Makefile - Directorio raíz.
#
# $Revision$
#
Root    := .
Subdirs := 

include $(Root)/rules.make

# Esto está pendiente
#
#jar:
#	@find . -name \*.class | xargs jar cvf manifest abalone.jar
#
#.PHONY: dclean
#dclean: clean
#	@-rm -fv abalone.jar; 

.PHONY: help
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

