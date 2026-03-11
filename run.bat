@echo off
echo Compiling and running Spark-E application...

REM Create directories if they don't exist
if not exist "target\classes" mkdir target\classes

REM Find all Java files and compile them
echo Compiling Java files...
dir /s /b src\main\java\*.java > sources.txt
javac -d target\classes -cp "target\classes;lib\*" @sources.txt

REM Copy resources
echo Copying resources...
xcopy /E /I /Y src\main\resources\* target\classes\

REM Run the application
echo Starting application...
java -cp "target\classes" com.electrician.spark_e.SparkEApplication

del sources.txt
