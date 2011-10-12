@echo off
cls
:start
echo ------------------
echo 1. update code
echo 2. clean target
echo 3. create package
echo 4. build site
echo 5. deploy
echo 6. clear screen
echo 7. quit
echo ------------------
CHOICE /N /C:1234567
set nr=%ERRORLEVEL%
IF %nr% EQU 1 goto lupdate
IF %nr% EQU 2 goto lclean
IF %nr% EQU 3 goto lpackage
IF %nr% EQU 4 goto lsite
IF %nr% EQU 5 goto ldeploy
IF %nr% EQU 6 goto lcls
IF %nr% EQU 7 goto end

:lupdate
cls
svn update
goto start

:lclean
cls
call mvn clean
goto start

:lpackage
cls
call mvn package
goto start

:lsite
cls
call mvn site
goto start

:ldeploy
cls
call mvn deploy
goto start

:lcls
cls
goto start

:end
exit