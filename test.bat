@ECHO OFF
REM executes UI tests
REM assume project is updated

IF [%1] == [] GOTO :usage

adb -s %1 push app\build\outputs\apk\debug\app-debug.apk /data/local/tmp/com.kidscademy.atlas.instruments
adb -s %1 shell pm install -t -r "/data/local/tmp/com.kidscademy.atlas.instruments"

adb -s %1 push app\build\outputs\apk\androidTest\debug\app-debug-androidTest.apk /data/local/tmp/com.kidscademy.atlas.instruments.test
adb -s %1 shell pm install -t -r "/data/local/tmp/com.kidscademy.atlas.instruments.test"

adb -s %1 shell am instrument -w -r -e debug false -e class 'com.kidscademy.atlas.tablet.AppConformanceTest' com.kidscademy.atlas.instruments.test/android.support.test.runner.AndroidJUnitRunner
adb -s %1 shell am instrument -w -r -e debug false -e class 'com.kidscademy.atlas.tablet.TabletReaderTest' com.kidscademy.atlas.instruments.test/android.support.test.runner.AndroidJUnitRunner
adb -s %1 shell am instrument -w -r -e debug false -e class 'com.kidscademy.atlas.tablet.AppConformanceSyncTest' com.kidscademy.atlas.instruments.test/android.support.test.runner.AndroidJUnitRunner
adb -s %1 shell am instrument -w -r -e debug false -e class 'com.kidscademy.atlas.tablet.TabletReaderSyncTest' com.kidscademy.atlas.instruments.test/android.support.test.runner.AndroidJUnitRunner

GOTO :eof

:usage
@ECHO ON
ECHO Usage: test <device>
ECHO adb devices -- list all available devices
