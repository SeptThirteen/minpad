package com.minpad;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * 设置对话框
 * 允许用户配置数字键盘快捷键
 */
public class SettingsDialog extends JDialog {
    
    private ActionExecutor actionExecutor;
    
    public SettingsDialog(ActionExecutor actionExecutor) {
        this.actionExecutor = actionExecutor;
        
        setTitle("MinPad - 快捷键设置");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(createSettingsIcon());
        
        initUI();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // 标题
        JLabel titleLabel = new JLabel("配置数字键盘快捷操作");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 快捷键列表
        JPanel listPanel = createKeyListPanel();
        mainPanel.add(new JScrollPane(listPanel), BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * 创建设置齿轮图标
     */
    private Image createSettingsIcon() {
        return IconFactory.createMinPadIcon(32);
    }
    
    private JPanel createKeyListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        String[] keyNames = {
            "NumPad 0", "NumPad 1", "NumPad 2", "NumPad 3", "NumPad 4",
            "NumPad 5", "NumPad 6", "NumPad 7", "NumPad 8", "NumPad 9",
            "NumPad +", "NumPad -", "NumPad *", "NumPad /", "NumPad Enter",
            "NumPad ."
        };
        
        for (int i = 0; i < keyNames.length; i++) {
            final int keyIndex = i;
            JPanel itemPanel = createKeyItemPanel(keyIndex, keyNames[i]);
            panel.add(itemPanel);
            panel.add(Box.createVerticalStrut(5));
        }
        
        return panel;
    }
    
    private JPanel createKeyItemPanel(int keyIndex, String keyName) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // 左侧：键名
        JLabel keyLabel = new JLabel(keyName);
        keyLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        keyLabel.setPreferredSize(new Dimension(100, 20));
        panel.add(keyLabel, BorderLayout.WEST);
        
        // 中间：当前配置
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        ActionExecutor.ActionConfig action = actionExecutor.getAction(keyIndex);
        
        JLabel nameLabel = new JLabel("名称: " + (action != null ? action.getName() : "未配置"));
        JLabel cmdLabel = new JLabel("命令: " + (action != null && action.getCommand() != null ? action.getCommand() : "无"));
        JLabel keyComboLabel = new JLabel("组合键: " + (action != null && action.getKeyCombination() != null ? action.getKeyCombination() : "无"));
        nameLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        cmdLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        keyComboLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        
        infoPanel.add(nameLabel);
        infoPanel.add(cmdLabel);
        infoPanel.add(keyComboLabel);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // 右侧：编辑按钮和重置按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        
        JButton editButton = new JButton("编辑");
        editButton.addActionListener(e -> editAction(keyIndex, keyName, nameLabel, cmdLabel, keyComboLabel));
        buttonPanel.add(editButton);
        
        // NumPad +/- 和 Enter 可以重置为默认控制
        if (keyIndex == 10 || keyIndex == 11 || keyIndex == 14) {
            JButton resetButton = new JButton("重置");
            resetButton.addActionListener(e -> resetToDefault(keyIndex, nameLabel, cmdLabel));
            buttonPanel.add(resetButton);
        }
        
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void resetToDefault(int keyIndex, JLabel nameLabel, JLabel cmdLabel) {
        ActionExecutor.ActionConfig defaultAction;
        if (keyIndex == 10) {
            defaultAction = new ActionExecutor.ActionConfig("增加音量", "__volume_up");
        } else if (keyIndex == 11) {
            defaultAction = new ActionExecutor.ActionConfig("减少音量", "__volume_down");
        } else if (keyIndex == 14) {
            defaultAction = new ActionExecutor.ActionConfig("播放/暂停", "__play_pause");
        } else {
            return;
        }
        
        actionExecutor.setAction(keyIndex, defaultAction);
        
        // 立即保存配置
        ConfigManager.saveConfig(actionExecutor.getAllActions());
        
        nameLabel.setText("名称: " + defaultAction.getName());
        cmdLabel.setText("命令: 无");
        
        String feature = keyIndex == 14 ? "播放/暂停" : "音量控制";
        JOptionPane.showMessageDialog(this,
                "已重置为默认" + feature + "功能！",
                "重置成功",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void editAction(int keyIndex, String keyName, JLabel nameLabel, JLabel cmdLabel, JLabel keyComboLabel) {
        // 音量控制键和Enter键的特殊处理
        if (keyIndex == 10 || keyIndex == 11) {
            String currentName = nameLabel.getText().replace("名称: ", "");
            int option = JOptionPane.showConfirmDialog(this,
                    "是否使用默认的音量控制功能？\n\n" +
                    "当前功能: " + currentName + "\n" +
                    "NumPad + : 增加音量\n" +
                    "NumPad - : 减少音量\n\n" +
                    "点击\"是\"使用音量控制，\"否\"自定义其他功能",
                    "配置 " + keyName,
                    JOptionPane.YES_NO_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                resetToDefault(keyIndex, nameLabel, cmdLabel);
                return;
            }
        } else if (keyIndex == 14) {
            String currentName = nameLabel.getText().replace("名称: ", "");
            int option = JOptionPane.showConfirmDialog(this,
                    "是否使用默认的播放/暂停功能？\n\n" +
                    "当前功能: " + currentName + "\n" +
                    "NumPad Enter: 播放/暂停音乐\n\n" +
                    "点击\"是\"使用播放/暂停，\"否\"自定义其他功能",
                    "配置 " + keyName,
                    JOptionPane.YES_NO_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                resetToDefault(keyIndex, nameLabel, cmdLabel);
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
            "编辑 " + keyName + " 快捷键",
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
                
                // 更新显示
                nameLabel.setText("名称: " + name);
                cmdLabel.setText("命令: " + (command.isEmpty() ? "无" : command));
                keyComboLabel.setText("组合键: " + (keyCombo.isEmpty() ? "无" : keyCombo));
                
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
}
