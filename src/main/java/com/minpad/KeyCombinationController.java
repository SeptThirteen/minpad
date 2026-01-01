package com.minpad;

import com.sun.jna.Library;
import com.sun.jna.Native;

import java.util.ArrayList;
import java.util.List;

/**
 * 组合键控制器
 * 支持模拟组合键如 Ctrl+Shift+A 等
 */
public class KeyCombinationController {

    // 修饰键虚拟键码
    private static final int VK_CONTROL = 0x11;
    private static final int VK_SHIFT = 0x10;
    private static final int VK_MENU = 0x12;     // Alt 键
    private static final int VK_LWIN = 0x5B;     // Windows 左键

    private User32 user32;

    public KeyCombinationController() {
        try {
            this.user32 = Native.load("user32", User32.class);
        } catch (Exception e) {
            System.err.println("加载 user32 库失败: " + e.getMessage());
        }
    }

    /**
     * 执行组合键
     * 例如: "ctrl+shift+a" "alt+s" "ctrl+c" 等
     * 
     * @param combination 组合键字符串，使用 + 分隔，如 "ctrl+shift+a"
     */
    public void executeKeyCombination(String combination) {
        if (combination == null || combination.isEmpty() || user32 == null) {
            System.err.println("无效的组合键或 user32 库未初始化");
            return;
        }

        try {
            List<Integer> keysToPress = new ArrayList<>();
            String[] parts = combination.toLowerCase().split("\\+");

            boolean hasCtrl = false;
            boolean hasShift = false;
            boolean hasAlt = false;
            boolean hasWin = false;
            int mainKeyCode = -1;

            // 解析组合键部分
            for (String part : parts) {
                part = part.trim();
                switch (part) {
                    case "ctrl":
                    case "control":
                        hasCtrl = true;
                        break;
                    case "shift":
                        hasShift = true;
                        break;
                    case "alt":
                        hasAlt = true;
                        break;
                    case "win":
                    case "windows":
                        hasWin = true;
                        break;
                    default:
                        // 这是主键
                        mainKeyCode = parseKeyString(part);
                        break;
                }
            }

            if (mainKeyCode == -1) {
                System.err.println("无效的组合键: " + combination);
                return;
            }

            // 按下修饰键
            if (hasCtrl) {
                keybd_event(VK_CONTROL, 0, 0);
            }
            if (hasShift) {
                keybd_event(VK_SHIFT, 0, 0);
            }
            if (hasAlt) {
                keybd_event(VK_MENU, 0, 0);
            }
            if (hasWin) {
                keybd_event(VK_LWIN, 0, 0);
            }

            // 按下主键
            Thread.sleep(50);
            keybd_event(mainKeyCode, 0, 0);
            Thread.sleep(50);

            // 释放主键
            keybd_event(mainKeyCode, 0, 2);
            Thread.sleep(50);

            // 释放修饰键（反向顺序）
            if (hasWin) {
                keybd_event(VK_LWIN, 0, 2);
            }
            if (hasAlt) {
                keybd_event(VK_MENU, 0, 2);
            }
            if (hasShift) {
                keybd_event(VK_SHIFT, 0, 2);
            }
            if (hasCtrl) {
                keybd_event(VK_CONTROL, 0, 2);
            }

        } catch (Exception e) {
            System.err.println("执行组合键失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 解析按键字符串为虚拟键码
     */
    private int parseKeyString(String key) {
        // 字母 A-Z
        if (key.length() == 1 && key.matches("[a-z]")) {
            return 0x41 + (key.charAt(0) - 'a');
        }

        // 数字 0-9
        if (key.length() == 1 && key.matches("[0-9]")) {
            return 0x30 + (key.charAt(0) - '0');
        }

        // 特殊键
        switch (key) {
            case "space":
                return 0x20;
            case "enter":
            case "return":
                return 0x0D;
            case "tab":
                return 0x09;
            case "escape":
            case "esc":
                return 0x1B;
            case "backspace":
            case "back":
                return 0x08;
            case "delete":
            case "del":
                return 0x46;
            case "insert":
                return 0x2D;
            case "home":
                return 0x24;
            case "end":
                return 0x23;
            case "pageup":
            case "pgup":
                return 0x21;
            case "pagedown":
            case "pgdn":
                return 0x22;
            case "left":
                return 0x25;
            case "right":
                return 0x27;
            case "up":
                return 0x26;
            case "down":
                return 0x28;
            case "f1":
                return 0x70;
            case "f2":
                return 0x71;
            case "f3":
                return 0x72;
            case "f4":
                return 0x73;
            case "f5":
                return 0x74;
            case "f6":
                return 0x75;
            case "f7":
                return 0x76;
            case "f8":
                return 0x77;
            case "f9":
                return 0x78;
            case "f10":
                return 0x79;
            case "f11":
                return 0x7A;
            case "f12":
                return 0x7B;
            case ",":
            case "comma":
                return 0xBC;
            case ".":
            case "period":
                return 0xBE;
            case ";":
            case "semicolon":
                return 0xBA;
            case "'":
            case "quote":
                return 0xDE;
            case "[":
            case "lbracket":
                return 0xDB;
            case "]":
            case "rbracket":
                return 0xDD;
            case "\\":
            case "backslash":
                return 0xDC;
            case "/":
            case "slash":
                return 0xBF;
            case "=":
            case "equal":
                return 0xBB;
            case "-":
            case "minus":
                return 0xBD;
            case "`":
            case "backtick":
            case "grave":
                return 0xC0;
            default:
                return -1;
        }
    }

    /**
     * 模拟按键事件
     */
    private void keybd_event(int vkCode, int scanCode, int flags) throws InterruptedException {
        if (user32 != null) {
            user32.keybd_event((byte) vkCode, (byte) scanCode, flags, 0);
        }
    }

    /**
     * User32 库接口
     */
    public interface User32 extends Library {
        void keybd_event(byte bVk, byte bScan, int dwFlags, long dwExtraInfo);
    }
}
