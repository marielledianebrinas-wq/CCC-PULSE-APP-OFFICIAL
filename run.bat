@echo off
setlocal
cd /d "%~dp0"
if not exist build mkdir build
javac -encoding UTF-8 -d build src\cccpulse\CccPulseApp.java
if errorlevel 1 exit /b 1
java -cp build cccpulse.CccPulseApp
