#
# $Id: Makefile,v 1.2 2002/11/03 11:36:10 keesj Exp $
#
all:compile

prepare:
	test -d build || mkdir  build
compile:prepare
	javac -d build src/wristcam/*/*.java
	cp -r src/wristcam/gui/resources build/wristcam/gui/
clean:
	rm -rf build
run:compile
	cd build && java wristcam.gui.Application
jar:compile
	cd build && jar cfm ../wqv-wristcam.jar ../resources/MANIFEST.MF wristcam
