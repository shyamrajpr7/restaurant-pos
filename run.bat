@echo off
title FolioDesk POS
echo.
echo  ==========================================
echo   FolioDesk POS - Restaurant Suite
echo  ==========================================
echo.

REM Check if compiled JAR exists
if exist "target\FolioDesk-POS.jar" (
    echo  Starting FolioDesk POS...
    java -jar target\FolioDesk-POS.jar
) else (
    echo  JAR not found. Compiling first...
    echo.
    call mvn package -q
    if %ERRORLEVEL% == 0 (
        echo  Build successful! Starting...
        java -jar target\FolioDesk-POS.jar
    ) else (
        echo  Build failed. Please check errors above.
        pause
    )
)
