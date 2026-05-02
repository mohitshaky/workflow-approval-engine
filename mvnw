#!/bin/sh
set -e
WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPS=".mvn/wrapper/maven-wrapper.properties"
if [ ! -f "$WRAPPER_JAR" ]; then
  mkdir -p .mvn/wrapper
  curl -s -L "$(grep -E '^wrapperUrl=' "$WRAPPER_PROPS" | cut -d= -f2)" -o "$WRAPPER_JAR"
fi
JAVA_HOME="${JAVA_HOME:-}"
exec "${JAVA_HOME:+$JAVA_HOME/bin/}java" -jar "$WRAPPER_JAR" -Dmaven.multiModuleProjectDirectory="$PWD" org.apache.maven.wrapper.MavenWrapperMain "$@"
