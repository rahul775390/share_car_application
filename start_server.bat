@echo off
set "JAVA_HOME=C:\Program Files\BellSoft\LibericaJDK-8-Full"
set "PATH=%JAVA_HOME%\bin;%PATH%"
"C:\Users\rahul\Downloads\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin\mvn.cmd" tomcat7:run
