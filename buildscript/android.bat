setlocal

cd ..
rd /s/q build\app\outputs
flutter build apk --release --split-debug-info=debugInfo --split-per-abi

endlocal