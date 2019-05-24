#!/bin/bash

# if []
# TMPPATH=$(find ~ -type d -name 'Printemps' -print -quit)
TMPPATH=$PWD

# java -cp "$TMPPATH/libraries/commons-math3-3.6.1/commons-math3-3.6.1.jar:$TMPPATH/server/classes/communication:$TMPPATH/server/classes/database:$TMPPATH/server/classes/heatControl:$TMPPATH/server/classes/machineLearning/scheduleControl" Server
java -Djdbc.drivers=com.mysql.jdbc.Driver -cp ".:/usr/share/java/RXTXcomm.jar:/usr/share/java/RXTXcomm.jar:$TMPPATH/libraries/mysql-connector-java-5.0.8/mysql-connector-java-5.0.8-bin.jar:$TMPPATH/libraries/commons-math3-3.6.1/commons-math3-3.6.1.jar:$TMPPATH/classes" Server
