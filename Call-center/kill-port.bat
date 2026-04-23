@echo off
echo Liberando puertos 8081...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
    taskkill /PID %%a /F 2>nul
)
echo Listo. Puedes ejecutar la app.
pause
