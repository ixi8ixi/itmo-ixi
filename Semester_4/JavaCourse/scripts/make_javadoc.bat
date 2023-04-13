@echo off
set outpath=..\..\java-advanced-2023\modules\info.kgeorgiy.java.advanced.implementor\info\kgeorgiy\java\advanced\implementor
set impath=..\java-solutions\info\kgeorgiy\ja\Belotserkovchenko\implementor\*.java

echo Creating javadoc ...
javadoc -private -d ..\javadoc %impath% %outpath%\ImplerException.java %outpath%\Impler.java %outpath%\JarImpler.java
echo Done!