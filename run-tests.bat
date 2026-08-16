@echo off
title Auto Servis - JUnit 5 Testovi
echo ======================================================================
echo    POKRETANJE JUNIT 5 TESTOVA (CS202)
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

if not exist target\test-classes (
    echo [INFO] Kompajliram testne klase...
    mkdir target\test-classes 2>nul
    %JAVAC_CMD% -encoding UTF-8 -cp "lib/*;target/classes" -d target/test-classes src/test/java/rs/autoservice/*.java
)

%JAVA_CMD% -cp "target/classes;target/test-classes;lib/*" rs.autoservice.TestRunner
pause
