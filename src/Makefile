#
# Makefile - Directorio raíz.
#
# $Revision$
#
Root    := .
Subdirs := atory

include $(Root)/rules.make

jar: all
	@find atory \( -name \*.class -or -name \*.png \) -print0 | xargs -0 jar cvf atory.jar
#	@jar cvf atory.jar atory

package: jar linux_package #win_package

win_package:
	@test -d win_package/atory || mkdir -p win_package/atory; \
	cd win_package/atory; \
	cp -v ../../atory.jar .; \
	cp -v ../../../win_atory.bat atory.bat; \
	cp -v ../../../lib/swt_win32.jar swt.jar; \
	cp -v ../../../lib/xpp3-1.1.3_7.jar .; \
	cp -v ../../../sysdeps/*.dll .; \
	cp -v ../../../sysdeps/javaw.exe.manifest .; \
	cd .. && zip -r9 atory_win.zip atory; \
	cd ..

linux_package:
	@test -d linux_package/atory || mkdir -p linux_package/atory; \
	cd linux_package/atory; \
	cp -v ../../atory.jar .; \
	cp -v ../../../linux_atory.run atory.run; \
	cp -v ../../../lib/swt.jar .; \
	cp -v ../../../lib/xpp3-1.1.3_7.jar .; \
	cp -v ../../../sysdeps/*.so .; \
	cd .. && zip -r9 atory_lin.zip atory; \
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

