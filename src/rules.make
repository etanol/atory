#
# rules.make - Reglas genéricas heredadas por todos los Makefiles de la
#              jerarquía.
#
# $Revision$
#
SHELL   := sh
Files   := $(First) $(wildcard *.java)
Targets := files
JarPath := $(Root)/../lib

# CLASSPATH for compilation
Classpath := $(Root):$(JarPath)/xpp3-1.1.3_7.jar:$(JarPath)/swt.jar

ifdef Subdirs
Targets += subdirs
endif

JAVACFLAGS := -deprecation -encoding UTF-8 #-encoding ISO-8859-1
ifdef final
export final
JAVACFLAGS += -g:none
else
JAVACFLAGS += -g
endif

all: $(Targets)

files: $(Files:.java=.class)

subdirs:
	@$(foreach d, $(Subdirs), echo "[$(d)]"; $(MAKE) -C $(d);)

%.class: %.java
	javac $(JAVACFLAGS) -classpath $(Classpath) $<

.PHONY: clean
clean:
	@-rm -fv *.class; \
	$(foreach d, $(Subdirs), echo "[$(d)]"; $(MAKE) -C $(d) clean;)

