package com.minpad;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

/**
 * Windows 音量控制器
 * 使用 JNA 调用 Windows API 模拟音量键按压
 */
public class AudioVolumeController {

    // 虚拟键码
    private static final int VK_VOLUME_UP = 0xAF;      // 音量上升键
    private static final int VK_VOLUME_DOWN = 0xAE;    // 音量下降键
    private static final int VK_VOLUME_MUTE = 0xAD;    // 静音键
    private static final int VOLUME_STEPS = 1;         // 每次按压的次数

    private User32 user32;

    public AudioVolumeController() {
        try {
            this.user32 = Native.load("user32", User32.class);
        } catch (Exception e) {
            System.err.println("加载 user32 库失败: " + e.getMessage());
        }
    }

    /**
     * 增加音量
     */
    public void volumeUp() {
        simulateKeyPress(VK_VOLUME_UP);
    }

    /**
     * 减少音量
     */
    public void volumeDown() {
        simulateKeyPress(VK_VOLUME_DOWN);
    }

    /**
     * 切换静音
     */
    public void toggleMute() {
        simulateKeyPress(VK_VOLUME_MUTE);
    }

    /**
     * 模拟键盘按压
     * 
     * @param vkCode 虚拟键码
     */
    private void simulateKeyPress(int vkCode) {
        if (user32 == null) {
            System.err.println("user32 库未初始化");
            return;
        }

        try {
            // 模拟多次按压以增加/减少音量的幅度
            for (int i = 0; i < VOLUME_STEPS; i++) {
                // KeyDown
                user32.keybd_event((byte) vkCode, (byte) 0, 0, 0);
                Thread.sleep(50);
                
                // KeyUp
                user32.keybd_event((byte) vkCode, (byte) 0, 2, 0); // 2 = KEYEVENTF_KEYUP
                Thread.sleep(50);
            }
        } catch (Exception e) {
            System.err.println("模拟按键失败: " + e.getMessage());
        }
    }

    /**
     * User32 库接口
     */
    public interface User32 extends Library {
        /**
         * 模拟键盘输入
         * 
         * @param bVk 虚拟键码
         * @param bScan 扫描码
         * @param dwFlags 标志位 (0 = KeyDown, 2 = KeyUp)
         * @param dwExtraInfo 额外信息
         */
        void keybd_event(byte bVk, byte bScan, int dwFlags, long dwExtraInfo);
    }
}

