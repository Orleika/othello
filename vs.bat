@echo off
title Othello Launcher

for /f "delims=" %%a in ('hostname') do @set HOSTNAME=%%a

:INPUT
echo your color^? ^<white / black^>
set /p COLOR=

:EXECUTE
if "%COLOR%" equ "white" (
  start java Server
  start java Controller %HOSTNAME% %COLOR%
  start java AI1 %HOSTNAME% black
) else if "%COLOR%" equ "black" (
  start java Server
  start java Controller %HOSTNAME% %COLOR%
  start java AI1 %HOSTNAME% white
) else (
  goto INPUT
)

:EXIT
echo Press any key quit.
pause > NUL
taskkill /im java.exe > NUL