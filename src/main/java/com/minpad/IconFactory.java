package com.minpad;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图标工厂
 * 统一管理应用中的所有图标
 */
public class IconFactory {

    private IconFactory() {
    }

    /**
     * 创建托盘和窗口使用的蓝色圆形 "#" 图标
     * 
     * @param size 图标大小（像素）
     * @return Image 对象
     */
    public static Image createMinPadIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // 抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Color accent = resolveAccentColor();
        Color glyphColor = resolveGlyphColor();

        // Fluent 风格：圆角方块背景 + 网格符号，降低视觉噪声。
        int arc = Math.max(4, Math.round(size * 0.34f));
        g.setColor(accent);
        g.fillRoundRect(0, 0, size, size, arc, arc);

        g.setColor(glyphColor);
        int stroke = Math.max(1, size / 8);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int margin = Math.max(3, size / 4);
        int left = margin;
        int right = size - margin;
        int top = margin;
        int bottom = size - margin;

        int v1 = left + (right - left) / 3;
        int v2 = left + ((right - left) * 2) / 3;
        int h1 = top + (bottom - top) / 3;
        int h2 = top + ((bottom - top) * 2) / 3;

        g.drawLine(v1, top, v1, bottom);
        g.drawLine(v2, top, v2, bottom);
        g.drawLine(left, h1, right, h1);
        g.drawLine(left, h2, right, h2);

        g.dispose();
        return image;
    }

    public static Icon createMenuItemIcon(char symbol) {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(resolveMenuIconColor());

        String[] fontCandidates = {
                "Segoe Fluent Icons",
                "Segoe MDL2 Assets",
                "Segoe UI Symbol",
                "Segoe UI",
                "Dialog"
        };

        Font font = pickAvailableFont(fontCandidates, Font.PLAIN, 12);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        String text = String.valueOf(symbol);
        int x = (size - fm.stringWidth(text)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, x, y);

        g.dispose();
        return new ImageIcon(image);
    }

    private static Color resolveAccentColor() {
        Color accent = UIManager.getColor("Component.focusColor");
        return accent != null ? accent : new Color(0, 120, 212);
    }

    private static Color resolveGlyphColor() {
        Color foreground = UIManager.getColor("Panel.foreground");
        if (foreground == null) {
            return Color.WHITE;
        }
        // 深色前景代表浅色主题，图标文字使用白色保证对比度。
        int luminance = (foreground.getRed() * 299 + foreground.getGreen() * 587 + foreground.getBlue() * 114) / 1000;
        return luminance < 128 ? Color.WHITE : new Color(35, 35, 35);
    }

    private static Color resolveMenuIconColor() {
        Color color = UIManager.getColor("MenuItem.foreground");
        return color != null ? color : new Color(30, 30, 30);
    }

    private static Font pickAvailableFont(String[] candidates, int style, int size) {
        String[] available = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String candidate : candidates) {
            for (String name : available) {
                if (name.equalsIgnoreCase(candidate)) {
                    return new Font(name, style, size);
                }
            }
        }
        return new Font(Font.SANS_SERIF, style, size);
    }
}
