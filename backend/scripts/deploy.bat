@echo off
REM XReport Docker Deploy Script (Windows)
REM Usage: deploy.bat

echo =========================================
echo   XReport Deployment Script
echo =========================================

REM Check if Docker is installed
where docker >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: Docker is not installed. Please install Docker first.
    exit /b 1
)

REM Check if docker-compose is installed
where docker-compose >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: docker-compose is not installed. Please install docker-compose first.
    exit /b 1
)

echo [1/5] Building Docker images...
docker-compose build

echo [2/5] Starting services...
docker-compose up -d

echo [3/5] Waiting for MySQL to be healthy...
for /L %%i in (1,1,30) do (
    docker-compose exec -T mysql mysqladmin ping -h localhost --silent >nul 2>nul
    if %ERRORLEVEL% equ 0 (
        echo MySQL is ready!
        goto :mysql_ready
    )
    echo Waiting for MySQL... (%%i/30)
    timeout /t 2 /nobreak >nul
)
:mysql_ready

echo [4/5] Waiting for XReport to start...
for /L %%i in (1,1,30) do (
    curl -s http://localhost:8080/actuator/health >nul 2>nul
    if %ERRORLEVEL% equ 0 (
        echo XReport is ready!
        goto :xreport_ready
    )
    curl -s http://localhost:8080/api/auth/health >nul 2>nul
    if %ERRORLEVEL% equ 0 (
        echo XReport is ready!
        goto :xreport_ready
    )
    echo Waiting for XReport... (%%i/30)
    timeout /t 2 /nobreak >nul
)
:xreport_ready

echo [5/5] Checking service status...
docker-compose ps

echo.
echo =========================================
echo   Deployment Complete!
echo =========================================
echo XReport: http://localhost:8080
echo MySQL:   localhost:3306
echo.
echo View logs: docker-compose logs -f
echo Stop services: docker-compose down
echo =========================================