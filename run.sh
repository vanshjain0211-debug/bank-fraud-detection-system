#!/bin/bash
set -e
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

# Ensure Java is usable; if default javac fails or is a broken wrapper, configure JDK path
if ! javac -version &>/dev/null; then
    if [ -d "/Library/Java/JavaVirtualMachines/jdk-26.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-26.jdk/Contents/Home"
        export PATH="$JAVA_HOME/bin:$PATH"
    fi
fi

javac $(find . -name "*.java")
java com.Main "$@"
