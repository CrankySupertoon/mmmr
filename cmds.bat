@echo off
cls
:start
echo ------------------
echo u. update code
echo c. clean target
echo p. create package
echo s. build site
echo d. deploy
echo w. clear window
echo q. quit
echo ------------------
CHOICE /N /C:ucpsdwq
set nr=%ERRORLEVEL%
IF %nr% EQU 1 goto lupdate
IF %nr% EQU 2 goto lclean
IF %nr% EQU 3 goto lpackage
IF %nr% EQU 4 goto lsite
IF %nr% EQU 5 goto ldeploy
IF %nr% EQU 6 goto lcls
IF %nr% EQU 7 goto end
goto start

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