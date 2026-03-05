package com.minpad;

import javax.swing.*;

/**
 * MinPad - 数字键盘快捷操作工具
 * 主入口类
 */
public class Main {
    public static void main(String[] args) {
        // 初始化 Fluent 风格主题
        try {
            ThemeManager.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 检查是否已有实例在运行
        if (!SingleInstanceLock.tryLock()) {
            // 直接在主线程显示对话框（阻塞）
            FluentDialogs.info(null,
                "MinPad - 提示",
                "MinPad 已经在运行中！\n\n" +
                "请在系统托盘中找到 MinPad 图标进行操作。\n" +
                "如需退出，请右键托盘图标选择退出。");
            System.exit(0);
            return;
        }

        // 确保在事件调度线程中运行
        SwingUtilities.invokeLater(() -> {
            try {
                NumPadListener listener = new NumPadListener();
                KeyboardHook keyboardHook = new KeyboardHook(actionIndex -> listener.getActionExecutor().executeAction(actionIndex));
                SystemTrayManager trayManager = new SystemTrayManager(listener, keyboardHook);
                
                keyboardHook.start();
                listener.start();
                trayManager.initialize();
                
                System.out.println("MinPad 已启动，监听数字键盘...");
            } catch (Exception e) {
                e.printStackTrace();
                FluentDialogs.error(null,
                    "错误",
                    "启动失败: " + e.getMessage());
                SingleInstanceLock.releaseLock();
                System.exit(1);
            }
        });
    }
}
