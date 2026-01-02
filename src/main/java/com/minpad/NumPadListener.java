package com.minpad;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 数字键盘监听器
 * 使用JNativeHook库监听全局键盘事件
 */
public class NumPadListener implements NativeKeyListener {
    
    private ActionExecutor actionExecutor;
    
    public NumPadListener() {
        this.actionExecutor = new ActionExecutor();
        
        // 禁用JNativeHook的日志输出
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
    }
    
    /**
     * 启动全局键盘监听
     */
    public void start() throws NativeHookException {
        if (!GlobalScreen.isNativeHookRegistered()) {
            GlobalScreen.registerNativeHook();
        }
        GlobalScreen.addNativeKeyListener(this);
    }
    
    /**
     * 停止监听
     */
    public void stop() {
        try {
            GlobalScreen.removeNativeKeyListener(this);
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int rawCode = e.getRawCode();
        int location = e.getKeyLocation();
        
        // 只处理来自数字键盘区域的按键（位置=4）
        // 必须同时满足：正确的原始码 AND 位置是数字键盘区域
        if (location != 4) {
            return; // 不是数字键盘区域，直接返回
        }

        // 所有数字键盘按键由低层钩子处理，这里跳过，避免重复触发
        if (rawCode == 96 || rawCode == 97 || rawCode == 98 || rawCode == 99 ||
            rawCode == 100 || rawCode == 101 || rawCode == 102 || rawCode == 103 ||
            rawCode == 104 || rawCode == 105 || rawCode == 106 || rawCode == 107 ||
            rawCode == 109 || rawCode == 110 || rawCode == 111 || rawCode == 13) {
            return;
        }
        
        // 使用原始码识别数字键盘按键（仅在NumLock开启状态下工作）
        switch (rawCode) {
            case 96: // NumPad 0
                handleAction(0, "NumPad 0");
                break;
            case 98: // NumPad 2
                handleAction(2, "NumPad 2");
                break;
            case 99: // NumPad 3
                handleAction(3, "NumPad 3");
                break;
            case 100: // NumPad 4
                handleAction(4, "NumPad 4");
                break;
            case 101: // NumPad 5
                handleAction(5, "NumPad 5");
                break;
            case 102: // NumPad 6
                handleAction(6, "NumPad 6");
                break;
            case 103: // NumPad 7
                handleAction(7, "NumPad 7");
                break;
            case 104: // NumPad 8
                handleAction(8, "NumPad 8");
                break;
            case 105: // NumPad 9
                handleAction(9, "NumPad 9");
                break;
            case 107: // NumPad + (Add)
                handleAction(10, "NumPad +");
                break;
            case 109: // NumPad - (Subtract)
                handleAction(11, "NumPad -");
                break;
            case 106: // NumPad * (Multiply)
                handleAction(12, "NumPad *");
                break;
            case 111: // NumPad / (Divide)
                handleAction(13, "NumPad /");
                break;
            case 13: // NumPad Enter
                handleAction(14, "NumPad Enter");
                break;
            case 110: // NumPad . (Decimal)
                handleAction(15, "NumPad .");
                break;
        }
    }
    
    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // 不处理按键释放事件
    }
    
    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // 不处理按键输入事件
    }
    
    /**
     * 处理快捷键动作
     */
    private void handleAction(int keyIndex, String keyName) {
        System.out.println("检测到按键: " + keyName);
        actionExecutor.executeAction(keyIndex);
    }
    
    public ActionExecutor getActionExecutor() {
        return actionExecutor;
    }
}

