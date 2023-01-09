#!/bin/sh
#
# In 2023 Ubuntu after apt-get install librxtx-java (and dpkg -L librxtx-java )
RXTX=/usr/share/java/RXTXcomm-2.2pre2.jar

java  -Djava.library.path=/usr/lib/jni -classpath wqv-wristcam.jar:${RXTX} wristcam.gui.Application
