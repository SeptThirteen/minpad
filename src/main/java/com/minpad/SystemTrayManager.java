package com.minpad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * 系统托盘管理器
 * 管理系统托盘图标和菜单
 */
public class SystemTrayManager {
    
    private TrayIcon trayIcon;
    private NumPadListener listener;
    private KeyboardHook keyboardHook;
    
    public SystemTrayManager(NumPadListener listener, KeyboardHook keyboardHook) {
        this.listener = listener;
        this.keyboardHook = keyboardHook;
    }
    
    /**
     * 初始化系统托盘
     */
    public void initialize() {
        if (!SystemTray.isSupported()) {
            System.err.println("系统不支持托盘图标");
            return;
        }
        
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = createTrayIcon();
            
            PopupMenu popup = createPopupMenu();
            trayIcon = new TrayIcon(image, "MinPad - 数字键盘快捷工具", popup);
            trayIcon.setImageAutoSize(true);
            
            // 双击托盘图标显示设置
            trayIcon.addActionListener(e -> showSettings());
            
            tray.add(trayIcon);
            
            // 显示启动通知
            trayIcon.displayMessage("MinPad", 
                "数字键盘快捷工具已启动", 
                TrayIcon.MessageType.INFO);
            
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 创建托盘图标
     */
    private Image createTrayIcon() {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        // 抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制背景圆形
        g.setColor(new Color(0, 120, 215));
        g.fillOval(0, 0, size, size);
        
        // 绘制数字 "#"
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        String text = "#";
        int x = (size - fm.stringWidth(text)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, x, y);
        
        g.dispose();
        return image;
    }
    
    /**
     * 创建右键菜单
     */
    private PopupMenu createPopupMenu() {
        PopupMenu popup = new PopupMenu();
        
        // 设置菜单
        MenuItem settingsItem = new MenuItem("设置快捷键...");
        settingsItem.addActionListener(e -> showSettings());
        popup.add(settingsItem);
        
        // 关于菜单
        MenuItem aboutItem = new MenuItem("关于");
        aboutItem.addActionListener(e -> showAbout());
        popup.add(aboutItem);
        
        popup.addSeparator();
        
        // 退出菜单
        MenuItem exitItem = new MenuItem("退出");
        exitItem.addActionListener(e -> exit());
        popup.add(exitItem);
        
        return popup;
    }
    
    /**
     * 显示设置对话框
     */
    private void showSettings() {
        SwingUtilities.invokeLater(() -> {
            SettingsDialog dialog = new SettingsDialog(listener.getActionExecutor());
            dialog.setVisible(true);
        });
    }
    
    /**
     * 显示关于对话框
     */
    private void showAbout() {
        String message = "MinPad - 数字键盘快捷工具\n\n" +
                        "版本: 1.0\n" +
                        "使用数字键盘快速执行常用操作\n\n" +
                        "默认快捷键:\n" +
                        "NumPad 1 - 打开记事本\n" +
                        "NumPad 2 - 打开计算器\n" +
                        "NumPad 3 - 打开浏览器\n" +
                        "NumPad 4 - 打开资源管理器\n" +
                        "NumPad 5 - 打开命令提示符\n" +
                        "NumPad 0 - 显示测试通知";
        
        JOptionPane.showMessageDialog(null, message, "关于 MinPad", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 退出程序
     */
    private void exit() {
        int result = JOptionPane.showConfirmDialog(null,
                "确定要退出 MinPad 吗？",
                "确认退出",
                JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (trayIcon != null) {
                SystemTray.getSystemTray().remove(trayIcon);
            }
            listener.stop();
            if (keyboardHook != null) {
                keyboardHook.stop();
            }
            System.exit(0);
        }
    }
}
