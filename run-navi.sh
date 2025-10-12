#!/usr/bin/env bash
set -euo pipefail

# Build if jar not present
if [ ! -f "target/navi.jar" ]; then
  ./mvnw -q -DskipTests=false clean package
fi

exec java -jar target/navi.jar "$@"
