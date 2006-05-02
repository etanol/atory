#
# rules.make - Reglas genéricas heredadas por todos los Makefiles de la
#              jerarquía.
#
# $Revision$
#
SHELL   := sh
Files   := $(wildcard *.java)
Targets := files

# Path separator
ifeq ($(shell uname),WindowsNT)
S := ;
else
S := :
endif

ifdef Subdirs
Targets += subdirs
endif

ifdef JarDeps
CompleteClasspath := $(Root)$(foreach j,$(JarDeps),$(S)$(Root)/../lib/$(j))
else
CompleteClasspath := $(Root)
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
	javac $(JAVACFLAGS) -classpath $(CompleteClasspath) $<

.PHONY: clean
clean:
	@-rm -fv *.class; \
	$(foreach d, $(Subdirs), echo "[$(d)]"; $(MAKE) -C $(d) clean;)

