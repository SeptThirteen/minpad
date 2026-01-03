package com.minpad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * 系统托盘管理器
 * 管理系统托盘图标和菜单
 */
public class SystemTrayManager {
    
    private TrayIcon trayIcon;
    private NumPadListener listener;
    private KeyboardHook keyboardHook;
    private JPopupMenu swingMenu;
    private JWindow popupWindow;
    
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

            swingMenu = createSwingMenu();
            popupWindow = new JWindow();
            popupWindow.setAlwaysOnTop(true);
            popupWindow.setSize(1, 1);

            trayIcon = new TrayIcon(image, "MinPad - 数字键盘快捷工具");
            trayIcon.setImageAutoSize(true);

            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
                        showSwingMenu(e.getX(), e.getY());
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
                        showSwingMenu(e.getX(), e.getY());
                    }
                }
            });
            // 左键双击仍然打开设置
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
        return IconFactory.createMinPadIcon(16);
    }

    /**
     * 从常用字体中选择一个可用的中文字体，避免方框。
     */
    private Font pickFont() {
        String[] candidates = {
                "Microsoft YaHei UI",
                "Microsoft YaHei",
                "SimSun",
                "Segoe UI",
                "Arial Unicode MS",
                "Dialog" // JVM 默认对话字体
        };
        String[] available = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String name : candidates) {
            for (String avail : available) {
                if (avail.equalsIgnoreCase(name)) {
                    System.out.println("Tray menu font: " + avail);
                    return new Font(avail, Font.PLAIN, 12);
                }
            }
        }
        System.out.println("Tray menu font fallback: " + Font.SANS_SERIF);
        return new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    }
    
    /**
     * 创建右键菜单
     */
    private JPopupMenu createSwingMenu() {
        JPopupMenu menu = new JPopupMenu();
        Font popupFont = pickFont();
        menu.setFont(popupFont);

        JMenuItem settingsItem = new JMenuItem("设置快捷键...");
        settingsItem.setFont(popupFont);
        settingsItem.addActionListener(e -> showSettings());
        menu.add(settingsItem);

        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.setFont(popupFont);
        aboutItem.addActionListener(e -> showAbout());
        menu.add(aboutItem);

        menu.addSeparator();

        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.setFont(popupFont);
        exitItem.addActionListener(e -> exit());
        menu.add(exitItem);

        return menu;
    }

    private void showSwingMenu(int x, int y) {
        SwingUtilities.invokeLater(() -> {
            if (swingMenu == null || popupWindow == null) {
                return;
            }
            popupWindow.setLocation(x, y);
            popupWindow.setVisible(true);
            swingMenu.addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    popupWindow.setVisible(false);
                }

                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {
                    popupWindow.setVisible(false);
                }
            });
            swingMenu.show(popupWindow.getContentPane(), 0, 0);
        });
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
                        "版本: 1.0.1\n\n" +
                        "一个轻量级的数字键盘快捷操作工具，\n" +
                        "让您快速执行常用应用和操作。\n\n" +
                        "功能特性:\n" +
                        "✓ 自定义数字键盘快捷键\n" +
                        "✓ 支持应用程序启动\n" +
                        "✓ 支持系统快捷键组合\n" +
                        "✓ 持久化配置保存\n\n" +
                        "开发者: SeptThirteen\n" +
                        "GitHub: github.com/SeptThirteen/minpad\n" +
                        "许可证: MIT License";
        
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
            // 保存配置
            ConfigManager.saveConfig(listener.getActionExecutor().getAllActions());
            
            if (trayIcon != null) {
                SystemTray.getSystemTray().remove(trayIcon);
            }
            listener.stop();
            if (keyboardHook != null) {
                keyboardHook.stop();
            }
            
            // 释放单实例锁
            SingleInstanceLock.releaseLock();
            
            System.exit(0);
        }
    }
}
