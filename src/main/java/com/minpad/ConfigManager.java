package com.minpad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * 配置管理器
 * 处理应用配置的加载和保存
 */
public class ConfigManager {
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".minpad";
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator + "config.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * 保存配置到文件
     */
    public static void saveConfig(Map<Integer, ActionExecutor.ActionConfig> actionMap) {
        try {
            // 确保配置目录存在
            Files.createDirectories(Paths.get(CONFIG_DIR));
            
            // 构建 JSON 配置
            JsonObject configJson = new JsonObject();
            JsonObject actionsJson = new JsonObject();
            
            for (Map.Entry<Integer, ActionExecutor.ActionConfig> entry : actionMap.entrySet()) {
                JsonObject actionJson = new JsonObject();
                ActionExecutor.ActionConfig config = entry.getValue();
                
                actionJson.addProperty("name", config.getName());
                if (config.getCommand() != null) {
                    actionJson.addProperty("command", config.getCommand());
                }
                if (config.getArgument() != null) {
                    actionJson.addProperty("argument", config.getArgument());
                }
                if (config.getKeyCombination() != null) {
                    actionJson.addProperty("keyCombination", config.getKeyCombination());
                }
                
                actionsJson.add(String.valueOf(entry.getKey()), actionJson);
            }
            
            configJson.add("actions", actionsJson);
            configJson.addProperty("version", "1.0.0");
            configJson.addProperty("lastModified", System.currentTimeMillis());
            
            // 写入文件
            String json = gson.toJson(configJson);
            Files.write(Paths.get(CONFIG_FILE), json.getBytes("UTF-8"));
            
            System.out.println("配置已保存: " + CONFIG_FILE);
        } catch (IOException e) {
            System.err.println("保存配置失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 从文件加载配置
     */
    public static Map<Integer, ActionExecutor.ActionConfig> loadConfig() {
        Map<Integer, ActionExecutor.ActionConfig> actionMap = new HashMap<>();
        
        try {
            Path configPath = Paths.get(CONFIG_FILE);
            
            // 如果配置文件不存在，返回空 Map（使用默认配置）
            if (!Files.exists(configPath)) {
                System.out.println("配置文件不存在，使用默认配置");
                return actionMap;
            }
            
            // 读取文件
            String json = new String(Files.readAllBytes(configPath), "UTF-8");
            JsonObject configJson = gson.fromJson(json, JsonObject.class);
            
            // 解析操作配置
            if (configJson.has("actions")) {
                JsonObject actionsJson = configJson.getAsJsonObject("actions");
                
                for (String key : actionsJson.keySet()) {
                    try {
                        int keyIndex = Integer.parseInt(key);
                        JsonObject actionJson = actionsJson.getAsJsonObject(key);
                        
                        String name = actionJson.get("name").getAsString();
                        String command = actionJson.has("command") ? actionJson.get("command").getAsString() : null;
                        String argument = actionJson.has("argument") ? actionJson.get("argument").getAsString() : null;
                        String keyCombination = actionJson.has("keyCombination") ? actionJson.get("keyCombination").getAsString() : null;
                        
                        ActionExecutor.ActionConfig config = new ActionExecutor.ActionConfig(
                            name, command, argument, keyCombination
                        );
                        actionMap.put(keyIndex, config);
                    } catch (NumberFormatException e) {
                        System.err.println("跳过无效的键索引: " + key);
                    }
                }
            }
            
            System.out.println("配置已加载: " + CONFIG_FILE);
        } catch (IOException e) {
            System.err.println("加载配置失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return actionMap;
    }
    
    /**
     * 获取配置文件路径
     */
    public static String getConfigFilePath() {
        return CONFIG_FILE;
    }
    
    /**
     * 检查是否存在保存的配置
     */
    public static boolean configExists() {
        return Files.exists(Paths.get(CONFIG_FILE));
    }
    
    /**
     * 删除配置文件
     */
    public static void deleteConfig() {
        try {
            Files.deleteIfExists(Paths.get(CONFIG_FILE));
            System.out.println("配置已删除");
        } catch (IOException e) {
            System.err.println("删除配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出配置到指定文件
     */
    public static boolean exportConfig(String exportPath) {
        try {
            Path configPath = Paths.get(CONFIG_FILE);
            if (!Files.exists(configPath)) {
                System.err.println("配置文件不存在，无法导出");
                return false;
            }
            
            // 读取当前配置并写入到目标位置
            String configContent = new String(Files.readAllBytes(configPath), "UTF-8");
            Files.write(Paths.get(exportPath), configContent.getBytes("UTF-8"));
            
            System.out.println("配置已导出到: " + exportPath);
            return true;
        } catch (IOException e) {
            System.err.println("导出配置失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 从指定文件导入配置
     */
    public static boolean importConfig(String importPath) {
        try {
            Path importFilePath = Paths.get(importPath);
            if (!Files.exists(importFilePath)) {
                System.err.println("导入文件不存在: " + importPath);
                return false;
            }
            
            // 验证文件是否是有效的 JSON
            String content = new String(Files.readAllBytes(importFilePath), "UTF-8");
            gson.fromJson(content, JsonObject.class);
            
            // 确保配置目录存在
            Files.createDirectories(Paths.get(CONFIG_DIR));
            
            // 将导入的配置写入到默认位置
            Files.write(Paths.get(CONFIG_FILE), content.getBytes("UTF-8"));
            
            System.out.println("配置已导入: " + CONFIG_FILE);
            return true;
        } catch (IOException e) {
            System.err.println("导入配置失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("导入文件格式错误: " + e.getMessage());
            return false;
        }
    }
}
