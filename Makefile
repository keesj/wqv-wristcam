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
