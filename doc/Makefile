#
# Makefile - Reglas para construír la documentación
#
# $Revision$
#

PDF_DOCS := propuesta.pdf # Poner aquí los ficheros PDF a generar

all: pdf

pdf: $(PDF_DOCS)

.SUFFIXES:
.SUFFIXES: .tex .dvi .ps .pdf .png .eps
.PRECIOUS: %.dvi %.ps %.eps

.ps.pdf:
	@echo "PS2PDF  $<" && ps2pdf $<

.dvi.ps:
	@echo "DVIPS   $<" && dvips $< 2>/dev/null

.tex.dvi:
	@set -e; echo "LATEX   $<"; for i in 1 2 3; do \
		latex -interaction=batchmode $< >/dev/null; \
	done

.png.eps:
	@echo "CONVERT $<" && convert $< $@


.PHONY: clean dclean help
clean:
	@-rm -fv *.dvi *.ps *.toc *.lof *.lot *.log *.aux

dclean: clean
	@-rm  -fv *.pdf *.eps

help:
	@echo 'Reglas disponibles, los nombres enre [] son los ficheros generados:';\
	echo '   pdf    Construye todos los documentos en formato PDF.';\
	echo '            [$(PDF_DOCS)]';\
	echo '   clean  Limpia los ficheros intermedios generados.';\
	echo '   dclean Limpia los documentos finales generados (implica "clean")';\
	echo '   help   Esta ayuda.';\
	echo '';\
	echo 'El destino por defecto es "pdf"'

