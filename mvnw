#!/bin/sh
# Apache Maven Wrapper script for Unix/Mac
# Downloads Maven automatically on first use

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(dirname "$0")}"
WRAPPER_DIR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper"
WRAPPER_JAR="$WRAPPER_DIR/maven-wrapper.jar"
WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

if [ -r "$WRAPPER_DIR/maven-wrapper.properties" ]; then
  DOWNLOAD_URL=$(grep wrapperUrl "$WRAPPER_DIR/maven-wrapper.properties" | cut -d= -f2)
fi

if [ ! -e "$WRAPPER_JAR" ]; then
  echo "Downloading $DOWNLOAD_URL"
  if command -v curl > /dev/null; then
    curl -o "$WRAPPER_JAR" "$DOWNLOAD_URL" -f
  elif command -v wget > /dev/null; then
    wget "$DOWNLOAD_URL" -O "$WRAPPER_JAR"
  else
    echo "No curl or wget found, cannot download wrapper."
    exit 1
  fi
fi

JAVA_CMD="${JAVA_HOME}/bin/java"
if [ -z "$JAVA_HOME" ] || [ ! -x "$JAVA_CMD" ]; then
  JAVA_CMD="java"
fi

exec "$JAVA_CMD" \
  -classpath "$WRAPPER_JAR" \
  "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
  $MAVEN_OPTS \
  $WRAPPER_LAUNCHER $MAVEN_CONFIG "$@"
