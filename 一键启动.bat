@echo off
title sqyy-all
echo ================================
echo    sqyy 社区医院系统 启动中...
echo ================================
echo.

start "sqyy-backend" "%~dp0启动后端.bat"
timeout /t 3 /nobreak >nul
start "sqyy-frontend" "%~dp0启动前端.bat"

echo.
echo ✅ 两个窗口已打开
echo    后端: http://localhost:8081
echo    前端: http://localhost:5173
echo.
pause
