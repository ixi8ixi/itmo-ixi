@echo off
set artifacts=java-advanced-2023\artifacts\info.kgeorgiy.java.advanced.implementor.jar
set impath=java-solutions\info\kgeorgiy\ja\Belotserkovchenko\implementor

echo Creating a jar file...

mkdir tmp
javac -classpath ..\..\%artifacts% -d .\tmp ..\%impath%\Implementor.java
cd tmp
jar cfm Implementor.jar ..\manifest_cfg.txt *
copy Implementor.jar .. >nul
cd ..
rmdir /s /q tmp

echo Done!