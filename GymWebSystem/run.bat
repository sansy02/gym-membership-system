@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo.
echo ==============================================
echo   Gym Membership System - Starting...
echo ==============================================
echo.

set JAVA="C:\Program Files\Apache NetBeans\jdk\bin\java.exe"
if not exist %JAVA% set JAVA=java

%JAVA% -cp "out;lib\mysql-connector-j-8.4.0.jar" gym.Main

pause
