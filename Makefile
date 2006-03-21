#
# $Id: Makefile,v 1.4 2006/03/21 14:44:19 keesj Exp $
#
RXTX= /usr/share/rxtx-2/lib/RXTXcomm.jar

all:compile

prepare:
	test -d build || mkdir  build
compile:prepare
	javac -target 1.5 -classpath $(RXTX) -d build src/wristcam/*/*.java
	cp -r src/wristcam/gui/resources build/wristcam/gui/
clean:
	rm -rf build
run:compile
	cd build && java  -Djava.library.path=/opt/rxtx-2/lib -classpath $(RXTX):. wristcam.gui.Application
jar:compile
	cd build && jar cfm ../wqv-wristcam.jar ../resources/MANIFEST.MF wristcam
