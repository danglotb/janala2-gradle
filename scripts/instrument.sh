#!/bin/bash

#
# Simple script to run catg to instrument classes, writes
# the instrumented classes and then run the file and print
# the instructions executed to the screen
#
# Example
#  ./instrument.sh MyClass

# Figure out script absolute path
pushd `dirname $0` > /dev/null
SCRIPT_DIR=`pwd`
popd > /dev/null

ROOT=`dirname $SCRIPT_DIR`
source "$SCRIPT_DIR/env.sh"

java -cp $CLASSPATH:. -javaagent:${ROOT}/lib/catg-dev.jar $@

