@echo off

set PATH=%PATH%;C:\Program Files\Java\jdk1.8.0_65\bin

javac -cp "lib/*" -d . ./src/crawler/*.java

jar cf TwitterGetter.jar ./crawler/*.class

java -cp "lib/*;TwitterGetter.jar" crawler.TwitterGetter %1 %2