#
# Makefile - Directorio raíz.
#
# $Revision$
#
Root    := .
Subdirs := atory

include $(Root)/rules.make

jar: all
	@find . -name \*.class | xargs jar cvf atory.jar

package: linux_package

linux_package: jar
	@test -d linux_package/atory || mkdir -p linux_package/atory; \
	cd linux_package/atory; \
	test -d images || mkdir images; \
	cp -v ../../atory.jar .; \
	cp -v ../../../linux_atory.run atory.run; \
	cp -v ../../../lib/swt.jar .; \
	cp -v ../../../lib/xpp3-1.1.3_7.jar .; \
	cp -v ../../../sysdeps/*.so .; \
	cp -v ../../atory/gui/images/*.png images; \
	cd .. && zip -r9 atory.zip atory; \
	cd ..

.PHONY: dclean help
dclean: clean
	@-rm -rfv atory.jar *_package

help:
	@echo 'Reglas genéricas:';\
	echo '   files    Compila los ficheros fuente del directorio actual.';\
	echo '   subdirs  Compila los ficheros fuente de los subdirectorios definidos';\
	echo '   clean    Limpia recursivamente los binarios generados.';\
	echo '';\
	echo 'Reglas para el directorio raíz:';\
	echo '   jar      Crea un fichero JAR que contiene todo el programa.';\
	echo '   package  Crea los paquetes con los binarios para su distribución';\
	echo '   dclean   Limpia el JAR generado y los paquetes (implica "clean")';\
	echo '   help     Esta ayuda.';\
	echo '';\
	echo 'La acción por defecto es "files". Si el Makefile define subdirectorios,';\
	echo 'también se procesarán recursivamente.'

