package com.minpad;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图标工厂
 * 统一管理应用中的所有图标
 */
public class IconFactory {

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
        
        // 绘制背景圆形
        g.setColor(new Color(0, 120, 215));
        g.fillOval(0, 0, size, size);
        
        // 绘制数字 "#"
        g.setColor(Color.WHITE);
        int fontSize = size > 32 ? 20 : (size > 16 ? 14 : 12);
        g.setFont(new Font("Arial", Font.BOLD, fontSize));
        FontMetrics fm = g.getFontMetrics();
        String text = "#";
        int x = (size - fm.stringWidth(text)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, x, y);
        
        g.dispose();
        return image;
    }
}
