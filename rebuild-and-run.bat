@echo off
chcp 65001 > nul
echo 正在停止旧进程...
taskkill /F /FI "WINDOWTITLE eq Administrator*" /FI "IMAGENAME eq java.exe" 2>nul
taskkill /F /FI "WINDOWTITLE eq *minpad*" /FI "IMAGENAME eq java.exe" 2>nul
timeout /t 1 /nobreak >nul

echo 正在清理并编译...
mvn clean package -q

if %ERRORLEVEL% EQU 0 (
    echo 编译成功！正在启动...
    start /B javaw -jar target\minpad-1.0.0.jar
    echo MinPad 已启动，请查看系统托盘
) else (
    echo 编译失败！
    pause
)
