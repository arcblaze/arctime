@echo off

echo Starting Application...

rem Go to the correct directory.
cd "%~p0%"

rem This is the class that will be launched.
set CLASS=com.arcblaze.arctime.Server

rem Set Java configuration options.
set JAVA_OPTS=%JAVA_OPTS% -ea
set JAVA_OPTS=%JAVA_OPTS% -Xmx256m
set JAVA_OPTS=%JAVA_OPTS% -Darctime.configurationFile=conf/arctime-config.properties
set JAVA_OPTS=%JAVA_OPTS% -Dlogback.configurationFile=conf/arctime-logging.xml

rem Build the classpath.
setlocal ENABLEDELAYEDEXPANSION
set CLASSPATH=conf;
set LIBS=arctime-dist\target\lib\*.jar
for %%i in (%LIBS%) do (
	set CLASSPATH=!CLASSPATH!;%%i
)

rem Start the app.
java %JAVA_OPTS% -classpath "%CLASSPATH%" %CLASS%

rem Pause so we can see the output.
pause

