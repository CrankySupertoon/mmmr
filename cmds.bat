@echo off
cls
:start
echo 1. update code
echo 2. clean target
echo 3. create package
echo 4. build site
echo 5. deploy
echo 6. quit
CHOICE /C:123456
set nr=%ERRORLEVEL%
IF %nr% EQU 1 goto l1
IF %nr% EQU 2 goto l2
IF %nr% EQU 3 goto l3
IF %nr% EQU 4 goto l4
IF %nr% EQU 5 goto l5
IF %nr% EQU 6 goto end
:l1
echo 1
svn update
goto start
:l2
echo 2
mvn clean
goto start
:l3
echo 3
mvn package
goto start
:l4
echo 4
mvn site
goto start
:l5
echo 5
mvn deploy
goto start
:end
echo end
exit