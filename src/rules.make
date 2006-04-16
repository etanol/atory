#
# rules.make - Reglas genéricas heredadas por todos los Makefiles de la
#              jerarquía.
#
# $Revision$
#
SHELL   := sh
Files   := $(wildcard *.java)
Targets := files

ifdef Subdirs
Targets += subdirs
endif

JAVACFLAGS := -deprecation #-encoding ISO-8859-1
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
	javac $(JAVACFLAGS) -classpath $(Root) $<

.PHONY: clean
clean:
	@-rm -fv *.class; \
	$(foreach d, $(Subdirs), echo "[$(d)]"; $(MAKE) -C $(d) clean;)

