@echo off
title Auto Servis - Generisanje JavaDoc Dokumentacije
echo ======================================================================
echo    GENERISANJE JAVADOC DOKUMENTACIJE (CS202)
echo ======================================================================

set JAVADOC_CMD=javadoc
where javadoc >nul 2>nul
if %errorlevel% neq 0 (
    if exist "C:\Program Files\Java\jdk-21\bin\javadoc.exe" (
        set JAVADOC_CMD="C:\Program Files\Java\jdk-21\bin\javadoc.exe"
    ) else if exist "C:\Program Files\Java\jdk-17\bin\javadoc.exe" (
        set JAVADOC_CMD="C:\Program Files\Java\jdk-17\bin\javadoc.exe"
    )
)

if not exist javadoc (
    mkdir javadoc 2>nul
)

echo [INFO] Pokrecem javadoc alat pomocu %JAVADOC_CMD%...
%JAVADOC_CMD% -encoding UTF-8 -charset UTF-8 -docencoding UTF-8 -d javadoc -cp "lib/*;src/main/resources" -subpackages rs.autoservice -sourcepath src/main/java -author -version

echo ======================================================================
echo JavaDoc HTML dokumentacija je uspesno kreirana u 'javadoc/' folderu!
echo Mozete otvoriti 'javadoc/index.html' u bilo kom web pretrazivacu.
echo ======================================================================
pause
