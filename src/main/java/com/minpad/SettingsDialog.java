package com.minpad;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * 设置对话框
 * 允许用户配置数字键盘快捷键
 * 使用物理键盘布局的可视化界面
 */
public class SettingsDialog extends JDialog {
    
    private ActionExecutor actionExecutor;
    private Map<Integer, JButton> keyButtons = new HashMap<>();
    
    // 颜色定义
    private static final Color CONFIGURED_COLOR = new Color(41, 98, 255);  // 深蓝色
    private static final Color UNCONFIGURED_COLOR = new Color(189, 189, 189);  // 灰色
    private static final Color HOVER_COLOR = new Color(100, 149, 237);  // 悬停时的蓝色
    
    public SettingsDialog(ActionExecutor actionExecutor) {
        this.actionExecutor = actionExecutor;
        
        setTitle("MinPad - 快捷键设置");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(createSettingsIcon());
        
        initUI();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // 标题
        JLabel titleLabel = new JLabel("数字键盘快捷键配置");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 键盘布局面板
        JPanel keyboardPanel = createKeyboardLayoutPanel();
        mainPanel.add(keyboardPanel, BorderLayout.CENTER);
        
        // 提示信息
        JLabel hintLabel = new JLabel("双击按键编辑配置 | 悬停查看详情");
        hintLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        hintLabel.setForeground(Color.GRAY);
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // 按钮面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(hintLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * 创建设置齿轮图标
     */
    private Image createSettingsIcon() {
        return IconFactory.createMinPadIcon(32);
    }
    
    /**
     * 创建物理键盘布局面板
     */
    private JPanel createKeyboardLayoutPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // 第一行: NumLock | / | * | -
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.gridheight = 1;
        panel.add(createDisabledKey("Num"), gbc);
        
        gbc.gridx = 1;
        panel.add(createKeyButton(13, "/"), gbc);
        
        gbc.gridx = 2;
        panel.add(createKeyButton(12, "*"), gbc);
        
        gbc.gridx = 3;
        panel.add(createKeyButton(11, "-"), gbc);
        
        // 第二行: 7 | 8 | 9 | + (加号占两行)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 1;
        panel.add(createKeyButton(7, "7"), gbc);
        
        gbc.gridx = 1;
        panel.add(createKeyButton(8, "8"), gbc);
        
        gbc.gridx = 2;
        panel.add(createKeyButton(9, "9"), gbc);
        
        gbc.gridx = 3; gbc.gridheight = 2;  // + 占两行
        panel.add(createKeyButton(10, "+"), gbc);
        
        // 第三行: 4 | 5 | 6
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.gridheight = 1;
        panel.add(createKeyButton(4, "4"), gbc);
        
        gbc.gridx = 1;
        panel.add(createKeyButton(5, "5"), gbc);
        
        gbc.gridx = 2;
        panel.add(createKeyButton(6, "6"), gbc);
        
        // 第四行: 1 | 2 | 3 | Enter (Enter占两行)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.gridheight = 1;
        panel.add(createKeyButton(1, "1"), gbc);
        
        gbc.gridx = 1;
        panel.add(createKeyButton(2, "2"), gbc);
        
        gbc.gridx = 2;
        panel.add(createKeyButton(3, "3"), gbc);
        
        gbc.gridx = 3; gbc.gridheight = 2;  // Enter 占两行
        panel.add(createKeyButton(14, "Enter"), gbc);
        
        // 第五行: 0 (占两格) | .
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.gridheight = 1;
        panel.add(createKeyButton(0, "0"), gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 1;
        panel.add(createKeyButton(15, "."), gbc);
        
        return panel;
    }
    
    /**
     * 创建不可配置的按键（如NumLock）
     */
    private JButton createDisabledKey(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        button.setBackground(new Color(220, 220, 220));
        button.setForeground(Color.GRAY);
        button.setEnabled(false);
        button.setFocusPainted(false);
        return button;
    }
    
    /**
     * 创建可配置的按键按钮
     */
    private JButton createKeyButton(int keyIndex, String keyLabel) {
        ActionExecutor.ActionConfig action = actionExecutor.getAction(keyIndex);
        String displayName = action != null ? action.getName() : "未设置";
        
        // 根据配置状态选择文字颜色
        String labelColor = action != null ? "#666666" : "#666666";  // 已配置:浅蓝灰色, 未配置:深灰
        String nameColor = action != null ? "#333333" : "#333333";   // 已配置:蓝色, 未配置:深色
        
        // 构建多行显示：上方键位标签，下方功能名称
        String buttonText = "<html><div style='text-align: center;'>" +
            "<span style='font-size: 9px; color: " + labelColor + ";'>" + keyLabel + "</span><br>" +
            "<span style='font-size: 9px; font-weight: bold; color: " + nameColor + ";'>" + displayName + "</span>" +
            "</div></html>";
        
        JButton button = new JButton(buttonText);
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        
        // 设置初始颜色
        updateButtonColor(button, action);
        
        // 设置Tooltip显示完整配置
        updateButtonTooltip(button, keyIndex, keyLabel, action);
        
        // 鼠标悬停效果
        button.addMouseListener(new MouseAdapter() {
            private Color originalColor;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                originalColor = button.getBackground();
                button.setBackground(HOVER_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (originalColor != null) {
                    button.setBackground(originalColor);
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {  // 双击编辑
                    editAction(keyIndex, keyLabel, button);
                }
            }
        });
        
        keyButtons.put(keyIndex, button);
        return button;
    }
    
    /**
     * 更新按钮颜色和文本
     */
    private void updateButtonColor(JButton button, ActionExecutor.ActionConfig action) {
        if (action != null) {
            button.setBackground(CONFIGURED_COLOR);
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
        } else {
            button.setBackground(UNCONFIGURED_COLOR);
            button.setForeground(Color.DARK_GRAY);
            button.setOpaque(true);
        }
    }
    
    /**
     * 更新按钮的Tooltip
     */
    private void updateButtonTooltip(JButton button, int keyIndex, String keyLabel, ActionExecutor.ActionConfig action) {
        if (action != null) {
            StringBuilder tooltip = new StringBuilder("<html>");
            tooltip.append("<b>").append(keyLabel).append("</b><br>");
            tooltip.append("名称: ").append(action.getName()).append("<br>");
            if (action.getCommand() != null) {
                tooltip.append("命令: ").append(action.getCommand()).append("<br>");
            }
            if (action.getArgument() != null) {
                tooltip.append("参数: ").append(action.getArgument()).append("<br>");
            }
            if (action.getKeyCombination() != null) {
                tooltip.append("组合键: ").append(action.getKeyCombination()).append("<br>");
            }
            tooltip.append("</html>");
            button.setToolTipText(tooltip.toString());
        } else {
            button.setToolTipText("<html><b>" + keyLabel + "</b><br>未配置<br>双击编辑</html>");
        }
    }
    
    /**
     * 编辑按键配置
     */
    private void editAction(int keyIndex, String keyLabel, JButton button) {
        // 音量控制键和Enter键的特殊处理
        if (keyIndex == 10 || keyIndex == 11) {
            ActionExecutor.ActionConfig currentAction = actionExecutor.getAction(keyIndex);
            String currentName = currentAction != null ? currentAction.getName() : "未设置";
            int option = JOptionPane.showConfirmDialog(this,
                    "是否使用默认的音量控制功能？\n\n" +
                    "当前功能: " + currentName + "\n" +
                    "NumPad + : 增加音量\n" +
                    "NumPad - : 减少音量\n\n" +
                    "点击\"是\"使用音量控制，\"否\"自定义其他功能",
                    "配置 " + keyLabel,
                    JOptionPane.YES_NO_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                resetToDefault(keyIndex, button);
                return;
            }
        } else if (keyIndex == 14) {
            ActionExecutor.ActionConfig currentAction = actionExecutor.getAction(keyIndex);
            String currentName = currentAction != null ? currentAction.getName() : "未设置";
            int option = JOptionPane.showConfirmDialog(this,
                    "是否使用默认的播放/暂停功能？\n\n" +
                    "当前功能: " + currentName + "\n" +
                    "NumPad Enter: 播放/暂停音乐\n\n" +
                    "点击\"是\"使用播放/暂停，\"否\"自定义其他功能",
                    "配置 " + keyLabel,
                    JOptionPane.YES_NO_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                resetToDefault(keyIndex, button);
                return;
            }
        }
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField();
        JTextField commandField = new JTextField();
        JTextField argumentField = new JTextField();
        JTextField keyComboField = new JTextField();
        
        // 如果已有配置，填充现有值
        ActionExecutor.ActionConfig existingAction = actionExecutor.getAction(keyIndex);
        if (existingAction != null) {
            nameField.setText(existingAction.getName());
            if (existingAction.getCommand() != null) {
                commandField.setText(existingAction.getCommand());
            }
            if (existingAction.getArgument() != null) {
                argumentField.setText(existingAction.getArgument());
            }
            if (existingAction.getKeyCombination() != null) {
                keyComboField.setText(existingAction.getKeyCombination());
            }
        }
        
        panel.add(new JLabel("名称:"));
        panel.add(nameField);
        panel.add(new JLabel("命令:"));
        panel.add(commandField);
        panel.add(new JLabel("参数:"));
        panel.add(argumentField);
        panel.add(new JLabel("组合键 (如 ctrl+shift+a):"));
        panel.add(keyComboField);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        JLabel infoLabel = new JLabel(
            "<html>按键说明:<br>" +
            "• 修饰键: ctrl/control, shift, alt, win/windows<br>" +
            "• 字母: a-z<br>" +
            "• 数字: 0-9<br>" +
            "• 特殊键: space, enter, tab, esc, backspace, delete, insert<br>" +
            "• 方向键: left, right, up, down<br>" +
            "• 符号: comma(,), period(.), semicolon(;), slash(/), backslash(\\)<br>" +
            "• 函数键: f1-f12<br>" +
            "例如: ctrl+s, shift+f5, alt+tab, ctrl+shift+comma</html>"
        );
        infoLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(this, 
            new Object[]{panel, infoPanel},
            "编辑 " + keyLabel + " 快捷键",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String command = commandField.getText().trim();
            String argument = argumentField.getText().trim();
            String keyCombo = keyComboField.getText().trim();
            
            if (!name.isEmpty() && (!command.isEmpty() || !keyCombo.isEmpty())) {
                ActionExecutor.ActionConfig newAction = new ActionExecutor.ActionConfig(
                    name,
                    command.isEmpty() ? null : command,
                    argument.isEmpty() ? null : argument,
                    keyCombo.isEmpty() ? null : keyCombo
                );
                actionExecutor.setAction(keyIndex, newAction);

                // 立即保存配置
                ConfigManager.saveConfig(actionExecutor.getAllActions());
                
                // 更新按钮显示
                String buttonText = "<html><div style='text-align: center;'>" +
                    "<span style='font-size: 9px; color: #E8F0FF;'>" + keyLabel + "</span><br>" +
                    "<span style='font-size: 12px; font-weight: bold; color: #FFFFFF;'>" + name + "</span>" +
                    "</div></html>";
                button.setText(buttonText);
                updateButtonColor(button, newAction);
                updateButtonTooltip(button, keyIndex, keyLabel, newAction);
                
                JOptionPane.showMessageDialog(this,
                    "快捷键配置已更新并保存！",
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "名称不能为空，且命令和组合键至少填一个！",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 重置为默认配置
     */
    private void resetToDefault(int keyIndex, JButton button) {
        ActionExecutor.ActionConfig defaultAction;
        String keyLabel;
        
        if (keyIndex == 10) {
            defaultAction = new ActionExecutor.ActionConfig("增加音量", "__volume_up");
            keyLabel = "+";
        } else if (keyIndex == 11) {
            defaultAction = new ActionExecutor.ActionConfig("减少音量", "__volume_down");
            keyLabel = "-";
        } else if (keyIndex == 14) {
            defaultAction = new ActionExecutor.ActionConfig("播放/暂停", "__play_pause");
            keyLabel = "Enter";
        } else {
            return;
        }
        
        actionExecutor.setAction(keyIndex, defaultAction);
        
        // 立即保存配置
        ConfigManager.saveConfig(actionExecutor.getAllActions());
        
        // 更新按钮显示
        String buttonText = "<html><div style='text-align: center;'>" +
            "<span style='font-size: 9px; color: #E8F0FF;'>" + keyLabel + "</span><br>" +
            "<span style='font-size: 12px; font-weight: bold; color: #FFFFFF;'>" + defaultAction.getName() + "</span>" +
            "</div></html>";
        button.setText(buttonText);
        updateButtonColor(button, defaultAction);
        updateButtonTooltip(button, keyIndex, keyLabel, defaultAction);
        
        String feature = keyIndex == 14 ? "播放/暂停" : "音量控制";
        JOptionPane.showMessageDialog(this,
                "已重置为默认" + feature + "功能！",
                "重置成功",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
