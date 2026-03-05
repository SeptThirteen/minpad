package com.minpad;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 统一的 Fluent 风格对话框入口。
 */
public final class FluentDialogs {

    private FluentDialogs() {
    }

    public static void info(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, buildMessagePanel(message), title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, buildMessagePanel(message), title, JOptionPane.ERROR_MESSAGE);
    }

    public static int yesNo(Component parent, String title, String message) {
        return JOptionPane.showConfirmDialog(
                parent,
                buildMessagePanel(message),
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
    }

    public static int confirm(Component parent, Object content, String title, int optionType, int messageType) {
        return JOptionPane.showConfirmDialog(parent, content, title, optionType, messageType);
    }

    private static JComponent buildMessagePanel(String message) {
        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(null);
        textArea.setFont(UIManager.getFont("Label.font"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(6, 4, 2, 4));
        panel.setBackground(UIManager.getColor("Panel.background"));
        panel.add(textArea, BorderLayout.CENTER);
        return panel;
    }
}
