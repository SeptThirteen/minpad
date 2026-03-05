@echo off
chcp 65001 > nul
echo 正在启动 MinPad（后台运行）...
start /B javaw -jar target\minpad.jar
echo MinPad 已在后台启动，请查看系统托盘图标
timeout /t 3
