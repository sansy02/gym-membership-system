@echo off
chcp 65001 >nul
cd /d "%~dp0"

set JAVAC="C:\Program Files\Apache NetBeans\jdk\bin\javac.exe"
if not exist %JAVAC% set JAVAC=javac

echo Compiling...
%JAVAC% -encoding UTF-8 -d out src\gym\Main.java src\gym\database\DBConnection.java src\gym\model\*.java src\gym\server\*.java

if %errorlevel%==0 (
    echo.
    echo Compile successful!
) else (
    echo.
    echo Compile FAILED. Check the error messages above.
)
pause
