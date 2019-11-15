ECHO OFF

SET package=com.kidscademy.atlas.instruments
SET port=5556
SET emulator_name=emulator-%port%

CALL:BuildApplication
CALL:BuildTests

FOR /F %%i in ('emulator -list-avds') DO (
	ECHO. 
	CALL:StartEmulator %%i
	CALL:InstallApplication %emulator_name%
	CALL:InstallTests %emulator_name%
	CALL:RunTests %emulator_name%
	CALL:StopEmulator %emulator_name%	
)

CALL:PublishApplication

ECHO Done.
EXIT /B 0

:BuildApplication
ECHO Building application...
CALL gradlew build
EXIT /B 0

:BuildTests
ECHO Building tests...
CALL gradlew assembleAndroidTest
EXIT /B 0

:InstallApplication
ECHO Install application on emulator %~1
adb -s %~1 push app\build\outputs\apk\debug\app-debug.apk /data/local/tmp/%package%
adb -s %~1 shell pm install -t -r "/data/local/tmp/%package%"
EXIT /B 0

:InstallTests
ECHO Install tests on emulator %~1
adb -s %~1 push app\build\outputs\apk\androidTest\debug\app-debug-androidTest.apk /data/local/tmp/%package%.test
adb -s %~1 shell pm install -t -r "/data/local/tmp/%package%.test"
EXIT /B 0

:StartEmulator
ECHO Starting emulator %~1 on port %port%...
SET /A second_port=%port% + 1
START cmd /c emulator -ports %port%,%second_port% @%~1
CALL:Sleep 30
EXIT /B 0

:StopEmulator
ECHO Stopping emulator %~1...
adb -s %~1 emu kill
CALL:Sleep 10
EXIT /B 0

:RunTests
ECHO Runnning tests for %~1...
EXIT /B 0

:PublishApplication
ECHO Publishing application...
ECHO gradlew build -Ppublish
EXIT /B 0

:Sleep
PING -4 -n %~1 localhost >NUL
EXIT /B 0
