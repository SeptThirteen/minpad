package com.minpad;

import javax.swing.*;
import java.awt.*;

/**
 * MinPad - 数字键盘快捷操作工具
 * 主入口类
 */
public class Main {
    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
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
                JOptionPane.showMessageDialog(null, 
                    "启动失败: " + e.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
