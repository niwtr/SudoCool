@echo off

set JAVA=%JAVA_HOME%\bin\java
SET OPENCV=D:\opencv\build\java\x64;D:\opencv\build\x64\vc12\bin;/usr/local/share/OpenCV/java/
SET OPENCV2=./build/x64

java -Djava.library.path=%OPENCV2% -jar ./SudoCool.jar

pause

