#
# $Id: Makefile,v 1.3 2003/07/07 08:27:03 keesj Exp $
#
all:compile

prepare:
	test -d build || mkdir  build
compile:prepare
	javac -target 1.3 -d build src/wristcam/*/*.java
	cp -r src/wristcam/gui/resources build/wristcam/gui/
clean:
	rm -rf build
run:compile
	cd build && java wristcam.gui.Application
jar:compile
	cd build && jar cfm ../wqv-wristcam.jar ../resources/MANIFEST.MF wristcam
