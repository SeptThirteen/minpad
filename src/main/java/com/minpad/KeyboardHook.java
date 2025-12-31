package com.minpad;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

/**
 * 低层键盘钩子，拦截数字键盘按键并可选择吞掉事件。
 */
public class KeyboardHook {

    private static final int WH_KEYBOARD_LL = 13;
    private static final int WM_KEYDOWN = 0x0100;
    private static final int WM_SYSKEYDOWN = 0x0104;
    private static final int WM_KEYUP = 0x0101;
    private static final int WM_SYSKEYUP = 0x0105;
    private static final int VK_NUMPAD0 = 0x60;
    private static final int VK_NUMPAD1 = 0x61;
    private static final int VK_NUMPAD2 = 0x62;
    private static final int VK_NUMPAD3 = 0x63;
    private static final int VK_NUMPAD4 = 0x64;
    private static final int VK_NUMPAD5 = 0x65;
    private static final int VK_NUMPAD6 = 0x66;
    private static final int VK_NUMPAD7 = 0x67;
    private static final int VK_NUMPAD8 = 0x68;
    private static final int VK_NUMPAD9 = 0x69;
    private static final int VK_MULTIPLY = 0x6A;
    private static final int VK_ADD = 0x6B;
    private static final int VK_SEPARATOR = 0x6C; // rarely used
    private static final int VK_SUBTRACT = 0x6D;
    private static final int VK_DECIMAL = 0x6E; // not used here
    private static final int VK_DIVIDE = 0x6F;
    private static final int VK_RETURN = 0x0D;

    private static final int LLKHF_EXTENDED = 0x01; // flag to distinguish NumPad Enter

    private final IntConsumer onKey;
    private WinUser.HHOOK hHook;
    private Thread hookThread;
    private volatile boolean running;
    private int threadId;
    private final Set<Integer> pressedKeys = ConcurrentHashMap.newKeySet();
    private final Map<Integer, ScheduledFuture<?>> repeatTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService repeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "minpad-key-repeat");
        t.setDaemon(true);
        return t;
    });

    public KeyboardHook(IntConsumer onKey) {
        this.onKey = onKey;
    }

    /** 启动钩子（在后台线程建立消息循环）。 */
    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        hookThread = new Thread(this::runLoop, "minpad-keyboard-hook");
        hookThread.setDaemon(true);
        hookThread.start();
    }

    /** 停止钩子并退出消息循环。 */
    public synchronized void stop() {
        running = false;
        if (threadId != 0) {
            User32.INSTANCE.PostThreadMessage(threadId, WinUser.WM_QUIT, null, null);
        }
    }

    private void runLoop() {
        threadId = Kernel32.INSTANCE.GetCurrentThreadId();
        HMODULE module = Kernel32.INSTANCE.GetModuleHandle(null);

        WinUser.LowLevelKeyboardProc keyboardProc = (nCode, wParam, info) -> {
            if (nCode >= 0) {
                int vkCode = info.vkCode;
                int msg = wParam.intValue();
                Integer actionIndex = mapVkToAction(vkCode, info.flags);

                // 只处理数字键盘映射，其它键直接放行
                if (actionIndex == null) {
                    return User32.INSTANCE.CallNextHookEx(
                        hHook,
                        nCode,
                        wParam,
                        new com.sun.jna.platform.win32.WinDef.LPARAM(Pointer.nativeValue(info.getPointer())));
                }

                if (msg == WM_KEYDOWN || msg == WM_SYSKEYDOWN) {
                    // 首次按下才触发，屏蔽系统自动重复
                    if (pressedKeys.add(vkCode)) {
                        trigger(actionIndex, vkCode);
                    }
                    return new LRESULT(1); // 吞掉数字键盘事件
                } else if (msg == WM_KEYUP || msg == WM_SYSKEYUP) {
                    pressedKeys.remove(vkCode);
                    stopRepeat(vkCode);
                    return new LRESULT(1); // 同样吞掉
                }
            }
                return User32.INSTANCE.CallNextHookEx(
                    hHook,
                    nCode,
                    wParam,
                    new com.sun.jna.platform.win32.WinDef.LPARAM(Pointer.nativeValue(info.getPointer())));
        };

        hHook = User32.INSTANCE.SetWindowsHookEx(WH_KEYBOARD_LL, keyboardProc, module, 0);
        if (hHook == null) {
            running = false;
            return;
        }

        // 简单消息循环，保持钩子存活
        WinUser.MSG msg = new WinUser.MSG();
        while (running && User32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
            User32.INSTANCE.TranslateMessage(msg);
            User32.INSTANCE.DispatchMessage(msg);
        }

        if (hHook != null) {
            User32.INSTANCE.UnhookWindowsHookEx(hHook);
            hHook = null;
        }

        // 退出时清理重复任务
        repeatTasks.values().forEach(f -> {
            if (f != null) {
                f.cancel(true);
            }
        });
        repeatExecutor.shutdownNow();
    }

    /** 将虚拟键码映射到动作索引。返回 null 表示不处理。 */
    private Integer mapVkToAction(int vkCode, int flags) {
        switch (vkCode) {
            case VK_NUMPAD0:
                return 0;
            case VK_NUMPAD1:
                return 1;
            case VK_NUMPAD2:
                return 2;
            case VK_NUMPAD3:
                return 3;
            case VK_NUMPAD4:
                return 4;
            case VK_NUMPAD5:
                return 5;
            case VK_NUMPAD6:
                return 6;
            case VK_NUMPAD7:
                return 7;
            case VK_NUMPAD8:
                return 8;
            case VK_NUMPAD9:
                return 9;
            case VK_ADD:
                return 10;
            case VK_SUBTRACT:
                return 11;
            case VK_MULTIPLY:
                return 12;
            case VK_DIVIDE:
                return 13;
            case VK_RETURN:
                // 只有数字键盘的 Enter 才处理（扩展标志为1）
                if ((flags & LLKHF_EXTENDED) == LLKHF_EXTENDED) {
                    return 14;
                }
                return null;
            default:
                return null;
        }
    }

    /**
     * 触发一次动作；对 + / - 支持长按循环，其他键仅一次。
     */
    private void trigger(Integer actionIndex, int vkCode) {
        if (actionIndex == null) return;

        // 先立即执行一次
        try {
            onKey.accept(actionIndex);
        } catch (Exception ignored) {
            // 防止回调异常导致输入阻塞
        }

        // 仅对 NumPad + / - 做长按循环
        if (vkCode == VK_ADD || vkCode == VK_SUBTRACT) {
            ScheduledFuture<?> future = repeatExecutor.scheduleAtFixedRate(() -> {
                try {
                    onKey.accept(actionIndex);
                } catch (Exception ignored) {
                }
            }, 150, 120, TimeUnit.MILLISECONDS); // 150ms 后开始，每 120ms 重复
            repeatTasks.put(vkCode, future);
        }
    }

    /**
     * 停止对应按键的长按循环。
     */
    private void stopRepeat(int vkCode) {
        ScheduledFuture<?> future = repeatTasks.remove(vkCode);
        if (future != null) {
            future.cancel(true);
        }
    }
}