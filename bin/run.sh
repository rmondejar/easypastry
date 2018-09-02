#!/bin/sh

JAVA_COMMAND=../../jdk1.6.0_05/bin/java
EASY_HOME=..

if [ -z "$CLASSPATH" ]; then
	CLASSPATH="."
fi

EASY_LIB=$EASY_HOME/lib/easypastry.jar;$EASY_HOME/lib/pastry.jar;$EASY_HOME/lib/bunshin.jar;$EASY_HOME/lib/jdom.jar;$EASY_HOME/lib/xstream.jar

$JAVA_COMMAND -cp $EASY_LIB: "$@"