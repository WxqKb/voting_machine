setlocal

ECHO "delete old version"
cd ..
rd /s/q build\windows\runner\Release
rd /s/q windows\setup\output

ECHO "flutter build"
flutter build windows --release && cd ..\windows\setup && call packet.bat

endlocal