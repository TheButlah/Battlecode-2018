#!/bin/sh
# This file should build and run your code.
# It will run if you're in nodocker mode on Mac or Linux,
# or if you're running in docker.

# Compile our code.
#echo javac $(find . -name '*.java') -classpath ../battlecode/java
#javac $(find . -name '*.java') -classpath ../battlecode/java

# Run our code.
#echo java -Xmx40m -classpath .:../battlecode/java Player
#java -Xmx40m -classpath .:../battlecode/java Player

#remember to disable assertions by removing the -ea flag when not debugging!
java -Xmx40m -jar -ea Battlecode-2018-0.1-all.jar