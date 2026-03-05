package com.minpad;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Fluent 风格主题管理器。
 */
public final class ThemeManager {

    public interface ThemeChangeListener {
        void onThemeChanged(ThemeMode requestedMode, ThemeMode effectiveMode);
    }

    public enum ThemeMode {
        LIGHT("light"),
        DARK("dark"),
        SYSTEM("system");

        private final String id;

        ThemeMode(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static ThemeMode fromId(String id) {
            if ("light".equalsIgnoreCase(id)) {
                return LIGHT;
            }
            if ("dark".equalsIgnoreCase(id)) {
                return DARK;
            }
            return SYSTEM;
        }
    }

    private static ThemeMode currentMode = ThemeMode.SYSTEM;
    private static ThemeMode currentEffectiveMode = ThemeMode.LIGHT;
    private static final List<ThemeChangeListener> listeners = new CopyOnWriteArrayList<>();

    private ThemeManager() {
    }

    public static void initialize() {
        ThemeMode mode = ThemeMode.fromId(ConfigManager.getThemeMode());
        applyTheme(mode, false);
    }

    public static ThemeMode getCurrentMode() {
        return currentMode;
    }

    public static ThemeMode getCurrentEffectiveMode() {
        return currentEffectiveMode;
    }

    public static boolean isDarkEffectiveTheme() {
        return currentEffectiveMode == ThemeMode.DARK;
    }

    public static void addThemeChangeListener(ThemeChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public static void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    public static void applyTheme(ThemeMode mode, boolean persist) {
        ThemeMode effectiveMode = mode == ThemeMode.SYSTEM ? detectSystemThemeMode() : mode;

        installLaf(effectiveMode);
        AppTheme.applyGlobalDefaults(effectiveMode == ThemeMode.DARK);

        // 触发所有已打开窗口刷新，确保运行时切换生效。
        FlatLaf.updateUI();

        currentMode = mode;
        currentEffectiveMode = effectiveMode;
        if (persist) {
            ConfigManager.setThemeMode(mode.getId());
        }

        notifyThemeChanged(mode, effectiveMode);
    }

    private static void notifyThemeChanged(ThemeMode requestedMode, ThemeMode effectiveMode) {
        Runnable notifier = () -> {
            for (ThemeChangeListener listener : listeners) {
                try {
                    listener.onThemeChanged(requestedMode, effectiveMode);
                } catch (Exception e) {
                    System.err.println("主题监听器执行失败: " + e.getMessage());
                }
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            notifier.run();
        } else {
            SwingUtilities.invokeLater(notifier);
        }
    }

    private static void installLaf(ThemeMode mode) {
        try {
            if (mode == ThemeMode.DARK) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
        } catch (Exception e) {
            System.err.println("设置 FlatLaf 失败，回退系统主题: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // 兜底失败时保持当前 LAF
            }
        }
    }

    private static ThemeMode detectSystemThemeMode() {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Object lightThemeFlag = toolkit.getDesktopProperty("win.darkMode.appsUseLightTheme");
            if (lightThemeFlag instanceof Boolean) {
                return ((Boolean) lightThemeFlag) ? ThemeMode.LIGHT : ThemeMode.DARK;
            }
        } catch (Exception ignored) {
            // 不支持该属性时回退浅色主题。
        }
        return ThemeMode.LIGHT;
    }
}
