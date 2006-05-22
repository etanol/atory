#
# rules.make - Reglas genéricas heredadas por todos los Makefiles de la
#              jerarquía.
#
# $Revision$
#
SHELL   := sh
Files   := $(First) $(wildcard *.java)
JarPath := $(Root)/../lib

# CLASSPATH for compilation
Classpath := $(Root):$(JarPath)/xpp3-1.1.3_7.jar:$(JarPath)/swt.jar

ifdef Subdirs
Targets := subdirs files
else
Targets := files
endif

JAVACFLAGS := -deprecation -encoding UTF-8 #-encoding ISO-8859-1
ifdef final
export final
JAVACFLAGS += -g:none
hint := [final]
else
JAVACFLAGS += -g
a :=
hint := $(a)       
endif

all: $(Targets)

files: $(Files:.java=.class)

subdirs:
	@$(foreach d, $(Subdirs), $(MAKE) -C $(d);)

%.class: %.java
	@echo "JAVAC $(hint) $<" && javac $(JAVACFLAGS) -classpath $(Classpath) $<

.PHONY: clean
clean:
	@-rm -fv *.class; \
	$(foreach d, $(Subdirs), $(MAKE) -C $(d) clean;)

