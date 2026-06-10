@echo off
setlocal enabledelayedexpansion

if not exist target\navi.jar (
  call mvnw.cmd -q -DskipTests=false clean package
  if errorlevel 1 goto :eof
)

java -jar target\navi.jar %*
