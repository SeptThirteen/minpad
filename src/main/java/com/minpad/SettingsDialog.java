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
    
    private JPanel createKeyListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        String[] keyNames = {
            "NumPad 0", "NumPad 1", "NumPad 2", "NumPad 3", "NumPad 4",
            "NumPad 5", "NumPad 6", "NumPad 7", "NumPad 8", "NumPad 9",
            "NumPad +", "NumPad -", "NumPad *", "NumPad /", "NumPad Enter"
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
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        ActionExecutor.ActionConfig action = actionExecutor.getAction(keyIndex);
        
        JLabel nameLabel = new JLabel("名称: " + (action != null ? action.getName() : "未配置"));
        JLabel cmdLabel = new JLabel("命令: " + (action != null && action.getCommand() != null ? action.getCommand() : "无"));
        nameLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        cmdLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        
        infoPanel.add(nameLabel);
        infoPanel.add(cmdLabel);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // 右侧：编辑按钮
        JButton editButton = new JButton("编辑");
        editButton.addActionListener(e -> editAction(keyIndex, keyName, nameLabel, cmdLabel));
        panel.add(editButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private void editAction(int keyIndex, String keyName, JLabel nameLabel, JLabel cmdLabel) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField();
        JTextField commandField = new JTextField();
        JTextField argumentField = new JTextField();
        
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
        }
        
        panel.add(new JLabel("名称:"));
        panel.add(nameField);
        panel.add(new JLabel("命令:"));
        panel.add(commandField);
        panel.add(new JLabel("参数:"));
        panel.add(argumentField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "编辑 " + keyName + " 快捷键", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String command = commandField.getText().trim();
            String argument = argumentField.getText().trim();
            
            if (!name.isEmpty() && !command.isEmpty()) {
                ActionExecutor.ActionConfig newAction = new ActionExecutor.ActionConfig(
                    name, 
                    command, 
                    argument.isEmpty() ? null : argument
                );
                actionExecutor.setAction(keyIndex, newAction);
                
                // 更新显示
                nameLabel.setText("名称: " + name);
                cmdLabel.setText("命令: " + command);
                
                JOptionPane.showMessageDialog(this, 
                    "快捷键配置已更新！", 
                    "成功", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "名称和命令不能为空！", 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
