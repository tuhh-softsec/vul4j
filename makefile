SOURCES = $(wildcard src/*.java)
CLASS_FILES = $(SOURCES:src/%.java=classes/net/sf/xslthl/%.class)

.PHONY: prepare all finish

all: prepare $(CLASS_FILES) finish

prepare:
	del filelist 2>nul

$(CLASS_FILES): classes/net/sf/xslthl/%.class: src/%.java
	@echo $< >> filelist

finish:
	if exist filelist javac -source 1.5 -target 1.5 -cp ..\saxon\saxon.jar;classes -d classes @filelist
