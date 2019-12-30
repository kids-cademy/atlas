@echo off
setlocal enableDelayedExpansion 

set package=com.kidscademy.atlas
set port=5556
set emulator_name=emulator-%port%

call :clean
call :build_application
call :build_tests

set avd_type=null
for /f %%a in ('emulator -list-avds') do (
	call :init_avd_type %%a
	call :start_emulator %%a
	call :install_application %emulator_name%
	call :install_tests %emulator_name%
	call :run_tests %emulator_name%
	call :stop_emulator %emulator_name%	
)

call :publish_application
echo Done.
exit /b 0

:init_avd_type
set avd=%~1

if "%avd:~0,5%"=="phone" (
	set avd_type=phone
) else (
	set avd_type=tablet
)

echo .
echo ======================================
echo %avd% - !avd_type!
echo ====================================== 
echo .
goto :eof

:clean
echo .
echo Clean build
echo -------------------------------------------
call gradlew clean
goto :eof

:build_application
echo .
echo Building application...
echo -------------------------------------------
call gradlew build
goto :eof

:build_tests
echo .
echo Building tests...
echo -------------------------------------------
call gradlew assembleAndroidTest
goto :eof

:install_application
echo .
echo Install application on emulator %~1
echo -------------------------------------------

adb -s %~1 push app\build\outputs\apk\debug\app-debug.apk /data/local/tmp/%package%
adb -s %~1 shell pm install -t -r "/data/local/tmp/%package%"
goto :eof

:install_tests
echo .
echo Install tests on emulator %~1
echo -------------------------------------------

adb -s %~1 push app\build\outputs\apk\androidTest\debug\app-debug-androidTest.apk /data/local/tmp/%package%.test
adb -s %~1 shell pm install -t -r "/data/local/tmp/%package%.test"
goto :eof

:start_emulator
echo .
echo Starting emulator %~1 on port %port%...
echo -------------------------------------------

set /A second_port=%port% + 1
start cmd /c emulator -ports %port%,%second_port% @%~1
call :sleep 40
goto :eof

:stop_emulator
echo .
echo Stopping emulator %~1...
echo -------------------------------------------
adb -s %~1 emu kill
call :sleep 10
goto :eof

:run_tests
echo .
echo Runnning tests for %~1...
echo -------------------------------------------

adb -s %~1 shell am instrument -w -r -e debug false -e class '%package%.%avd_type%.AppConformanceTest' %package%.test/androidx.test.runner.AndroidJUnitRunner
adb -s %~1 shell am instrument -w -r -e debug false -e class '%package%.%avd_type%.ReaderTest' %package%.test/androidx.test.runner.AndroidJUnitRunner
goto :eof

:publish_application
echo .
echo Publishing application...
echo -------------------------------------------
call gradlew build -Ppublish
goto :eof

:sleep
ping -4 -n %~1 localhost >NUL
goto :eof
