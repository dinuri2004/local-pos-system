@echo off
TITLE Homeware POS System - Kiosk Mode

echo Starting Homeware POS Terminal Backend...

:: Start Spring Boot backend silently in the background
start /min java -jar D:\NSBM\Projects\POS\pos-backend\target\pos-backend-0.0.1-SNAPSHOT.jar

:: Wait a few seconds for the backend to start up
timeout /t 8 /nobreak > NUL

echo Launching POS Interface...

:: Launch Google Chrome (or Edge) in Kiosk Mode pointing to the unified port 9090
IF EXIST "C:\Program Files\Google\Chrome\Application\chrome.exe" (
    start "" "C:\Program Files\Google\Chrome\Application\chrome.exe" --kiosk --kiosk-printing http://localhost:9090
) ELSE (
    :: Fallback to Microsoft Edge if Chrome is not installed
    start "" "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe" --kiosk --kiosk-printing http://localhost:9090
)

exit
