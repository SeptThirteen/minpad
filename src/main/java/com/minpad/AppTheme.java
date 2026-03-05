package com.minpad;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 应用主题常量和全局 UI 默认值。
 */
public final class AppTheme {
    private static final Color LIGHT_ACCENT = new Color(0, 120, 212);
    private static final Color DARK_ACCENT = new Color(96, 165, 250);
    private static final String CJK_PROBE_TEXT = "中文字体显示";

    private AppTheme() {
    }

    public static void applyGlobalDefaults(boolean dark) {
        Color accent = dark ? DARK_ACCENT : LIGHT_ACCENT;
        int arc = 10;

        UIManager.put("Component.arc", arc);
        UIManager.put("Button.arc", arc);
        UIManager.put("TextComponent.arc", arc);
        UIManager.put("ScrollBar.thumbArc", arc);
        UIManager.put("CheckBox.arc", arc);
        UIManager.put("ProgressBar.arc", arc);
        UIManager.put("Component.focusWidth", 1);

        UIManager.put("Button.focusedBorderColor", new ColorUIResource(accent));
        UIManager.put("Component.focusColor", new ColorUIResource(accent));
        UIManager.put("TitlePane.unifiedBackground", Boolean.TRUE);

        UIManager.put("defaultFont", new FontUIResource(resolvePreferredFont()));
    }

    private static Font resolvePreferredFont() {
        String[] candidates = {
                "Microsoft YaHei UI",
                "Microsoft YaHei",
            "SimSun",
            "SimHei",
            "Segoe UI Variable",
            "Segoe UI",
                "Dialog"
        };

        String[] availableFonts = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        Set<String> normalized = new HashSet<>();
        for (String name : availableFonts) {
            normalized.add(name.toLowerCase());
        }

        for (String candidate : candidates) {
            if (normalized.contains(candidate.toLowerCase())) {
                Font font = new Font(candidate, Font.PLAIN, 13);
                // 避免选择不支持中文字形的字体，防止界面出现方块乱码。
                if (font.canDisplayUpTo(CJK_PROBE_TEXT) == -1) {
                    return font;
                }
            }
        }

        // 兜底：至少返回 JVM 复合字体，通常能显示 CJK。
        Font dialog = new Font("Dialog", Font.PLAIN, 13);
        if (dialog.canDisplayUpTo(CJK_PROBE_TEXT) == -1) {
            return dialog;
        }

        return new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    }
}
