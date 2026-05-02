@ECHO OFF
SETLOCAL
set WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar
set WRAPPER_PROPS=.mvn\wrapper\maven-wrapper.properties
IF NOT EXIST %WRAPPER_JAR% (
  mkdir .mvn\wrapper 2>NUL
  powershell -Command "(New-Object Net.WebClient).DownloadFile((Get-Content %WRAPPER_PROPS% | Select-String '^wrapperUrl=' | ForEach-Object { $_.ToString().Split('=')[1] }), '%WRAPPER_JAR%')"
)
set JAVA_EXE=java
%JAVA_EXE% -jar %WRAPPER_JAR% -Dmaven.multiModuleProjectDirectory=%CD% org.apache.maven.wrapper.MavenWrapperMain %*
ENDLOCAL
