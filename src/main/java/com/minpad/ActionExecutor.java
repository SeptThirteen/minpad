package com.minpad;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 动作执行器
 * 管理和执行数字键盘对应的快捷操作
 */
public class ActionExecutor {
    
    private Map<Integer, ActionConfig> actionMap;
    private AudioVolumeController audioController;
    
    public ActionExecutor() {
        actionMap = new HashMap<>();
        audioController = new AudioVolumeController();
        
        // 尝试从保存的配置加载，如果没有则使用默认配置
        if (ConfigManager.configExists()) {
            Map<Integer, ActionConfig> loadedConfig = ConfigManager.loadConfig();
            if (!loadedConfig.isEmpty()) {
                actionMap.putAll(loadedConfig);
                initializeSpecialActions();
                return;
            }
        }
        
        // 使用默认配置
        initializeDefaultActions();
    }
    
    /**
     * 初始化默认操作
     */
    private void initializeDefaultActions() {
        // NumPad 1: 打开记事本
        actionMap.put(1, new ActionConfig("打开记事本", "notepad.exe"));
        
        // NumPad 2: 打开计算器
        actionMap.put(2, new ActionConfig("打开计算器", "calc.exe"));
        
        // NumPad 3: 打开浏览器
        actionMap.put(3, new ActionConfig("打开浏览器", "explorer.exe", "https://www.google.com"));
        
        // NumPad 4: 打开文件资源管理器
        actionMap.put(4, new ActionConfig("打开资源管理器", "explorer.exe"));
        
        // NumPad 5: 打开命令提示符
        actionMap.put(5, new ActionConfig("打开CMD", "cmd.exe"));
        
        // NumPad 0: 显示通知
        actionMap.put(0, new ActionConfig("测试通知", null) {
            @Override
            public void execute() {
                showNotification("MinPad", "这是一个测试通知！");
            }
        });
        
        // NumPad +: 增加音量（默认）
        actionMap.put(10, new ActionConfig("增加音量", "__volume_up"));
        
        // NumPad -: 减少音量（默认）
        actionMap.put(11, new ActionConfig("减少音量", "__volume_down"));
        
        // NumPad Enter: 播放/暂停（默认）
        actionMap.put(14, new ActionConfig("播放/暂停", "__play_pause"));
        
        initializeSpecialActions();
    }
    
    /**
     * 初始化特殊命令的处理
     */
    private void initializeSpecialActions() {
        // 为特殊命令设置执行逻辑
        if (actionMap.get(10) != null) {
            ActionConfig config = actionMap.get(10);
            config.setAudioController(audioController);
        }
        if (actionMap.get(11) != null) {
            ActionConfig config = actionMap.get(11);
            config.setAudioController(audioController);
        }
        if (actionMap.get(14) != null) {
            ActionConfig config = actionMap.get(14);
            config.setAudioController(audioController);
        }
    }
    
    /**
     * 执行指定键的操作
     */
    public void executeAction(int keyIndex) {
        ActionConfig action = actionMap.get(keyIndex);
        if (action != null) {
            try {
                action.execute();
                System.out.println("执行操作: " + action.getName());
            } catch (Exception e) {
                System.err.println("执行操作失败: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("该按键未配置操作");
        }
    }
    
    /**
     * 设置指定键的操作
     */
    public void setAction(int keyIndex, ActionConfig action) {
        actionMap.put(keyIndex, action);
    }
    
    /**
     * 获取指定键的操作
     */
    public ActionConfig getAction(int keyIndex) {
        return actionMap.get(keyIndex);
    }
    
    /**
     * 获取所有操作配置
     */
    public Map<Integer, ActionConfig> getAllActions() {
        return new HashMap<>(actionMap);
    }
    
    /**
     * 显示系统通知
     */
    private void showNotification(String title, String message) {
        if (SystemTray.isSupported()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();
                if (tray.getTrayIcons().length > 0) {
                    TrayIcon trayIcon = tray.getTrayIcons()[0];
                    trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 动作配置类
     */
    public static class ActionConfig {
        private String name;
        private String command;
        private String argument;
        private String keyCombination;  // 组合键，如 "ctrl+shift+a"
        private transient AudioVolumeController audioController;  // 不序列化
        
        public ActionConfig(String name, String command) {
            this(name, command, null, null);
        }
        
        public ActionConfig(String name, String command, String argument) {
            this(name, command, argument, null);
        }
        
        public ActionConfig(String name, String command, String argument, String keyCombination) {
            this.name = name;
            this.command = command;
            this.argument = argument;
            this.keyCombination = keyCombination;
        }
        
        public void execute() throws IOException {
            // 处理特殊命令
            if (command != null && command.startsWith("__")) {
                if ("__volume_up".equals(command) && audioController != null) {
                    audioController.volumeUp();
                    return;
                } else if ("__volume_down".equals(command) && audioController != null) {
                    audioController.volumeDown();
                    return;
                } else if ("__play_pause".equals(command) && audioController != null) {
                    audioController.playPause();
                    return;
                }
            }
            
            // 如果配置了组合键，优先执行组合键
            if (keyCombination != null && !keyCombination.isEmpty()) {
                KeyCombinationController controller = new KeyCombinationController();
                controller.executeKeyCombination(keyCombination);
                return;
            }
            
            // 否则执行命令
            if (command != null) {
                ProcessBuilder pb;
                if (argument != null) {
                    pb = new ProcessBuilder(command, argument);
                } else {
                    pb = new ProcessBuilder(command);
                }
                pb.start();
            }
        }
        
        public String getName() {
            return name;
        }
        
        public String getCommand() {
            return command;
        }
        
        public String getArgument() {
            return argument;
        }
        
        public String getKeyCombination() {
            return keyCombination;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public void setCommand(String command) {
            this.command = command;
        }
        
        public void setArgument(String argument) {
            this.argument = argument;
        }
        
        public void setKeyCombination(String keyCombination) {
            this.keyCombination = keyCombination;
        }
        
        public void setAudioController(AudioVolumeController audioController) {
            this.audioController = audioController;
        }
    }
}
