@echo off
title Auto Servis - Backend Server
echo ======================================================================
echo    POKRETANJE BACKEND SERVERA (AUTO SERVIS MANAGEMENT SYSTEM)
echo ======================================================================

set JAVAC_CMD=javac
where javac >nul 2>nul
if %errorlevel% neq 0 (
    if exist "C:\Program Files\Java\jdk-21\bin\javac.exe" (
        set JAVAC_CMD="C:\Program Files\Java\jdk-21\bin\javac.exe"
    ) else if exist "C:\Program Files\Java\jdk-17\bin\javac.exe" (
        set JAVAC_CMD="C:\Program Files\Java\jdk-17\bin\javac.exe"
    )
)

set JAVA_CMD=java
where java >nul 2>nul
if %errorlevel% neq 0 (
    if exist "C:\Program Files\Java\jdk-21\bin\java.exe" (
        set JAVA_CMD="C:\Program Files\Java\jdk-21\bin\java.exe"
    ) else if exist "C:\Program Files\Java\jdk-17\bin\java.exe" (
        set JAVA_CMD="C:\Program Files\Java\jdk-17\bin\java.exe"
    )
)

if not exist target\classes (
    echo [INFO] Kompajliram izvorni kod...
    mkdir target\classes 2>nul
    %JAVAC_CMD% -encoding UTF-8 -cp "lib/*;src/main/resources" -d target/classes src/main/java/rs/autoservice/model/*.java src/main/java/rs/autoservice/database/*.java src/main/java/rs/autoservice/dao/*.java src/main/java/rs/autoservice/service/*.java src/main/java/rs/autoservice/util/*.java src/main/java/rs/autoservice/server/*.java src/main/java/rs/autoservice/client/*.java src/main/java/rs/autoservice/controller/*.java src/main/java/rs/autoservice/*.java
    xcopy /s /y /q src\main\resources\* target\classes\ >nul
)

echo [INFO] Pokrecem AppServer na portu 8888...
%JAVA_CMD% -cp "target/classes;lib/*" rs.autoservice.server.AppServer
pause
