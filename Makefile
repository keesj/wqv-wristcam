#
# $Id: Makefile,v 1.4 2006/03/21 14:44:19 keesj Exp $
#
RXTX= /usr/share/rxtx-2/lib/RXTXcomm.jar
# in 2023 Ubuntu after apt-get install librxtx-java (and dpkg -L librxtx-java )
RXTX= /usr/share/java/RXTXcomm-2.2pre2.jar

all:compile

prepare:
	test -d build || mkdir  build
compile:prepare
	javac  -classpath $(RXTX) -d build src/wristcam/*/*.java
	cp -r src/wristcam/gui/resources build/wristcam/gui/
clean:
	rm -rf build
run:compile
	cd build && java  -Djava.library.path=/usr/lib/jni -classpath $(RXTX):. wristcam.gui.Application
jar:compile
	cd build && jar cfm ../wqv-wristcam.jar ../resources/MANIFEST.MF wristcam
