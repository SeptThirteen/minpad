package com.minpad;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * 单实例锁管理器
 * 确保应用程序同时只有一个实例运行
 */
public class SingleInstanceLock {
    
    private static final String LOCK_FILE_NAME = ".minpad.lock";
    private static FileLock lock;
    private static FileChannel channel;
    private static RandomAccessFile file;
    
    /**
     * 尝试获取应用程序锁
     * @return true 如果成功获取锁（没有其他实例运行），false 如果已有实例运行
     */
    public static boolean tryLock() {
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            File lockFile = new File(tempDir, LOCK_FILE_NAME);
            
            // 如果锁文件存在但无法删除（说明被占用），则已有实例运行
            if (lockFile.exists()) {
                try {
                    file = new RandomAccessFile(lockFile, "rw");
                    channel = file.getChannel();
                    lock = channel.tryLock();
                    
                    if (lock == null) {
                        // 无法获取锁，已有实例运行
                        closeResources();
                        return false;
                    }
                } catch (IOException e) {
                    // 文件被占用，已有实例运行
                    closeResources();
                    return false;
                }
            } else {
                // 创建新的锁文件
                file = new RandomAccessFile(lockFile, "rw");
                channel = file.getChannel();
                lock = channel.tryLock();
                
                if (lock == null) {
                    closeResources();
                    return false;
                }
            }
            
            // 添加关闭钩子，在程序退出时释放锁
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                releaseLock();
            }));
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            closeResources();
            return false;
        }
    }
    
    /**
     * 释放锁
     */
    public static void releaseLock() {
        try {
            if (lock != null && lock.isValid()) {
                lock.release();
            }
            closeResources();
            
            // 删除锁文件
            String tempDir = System.getProperty("java.io.tmpdir");
            File lockFile = new File(tempDir, LOCK_FILE_NAME);
            lockFile.delete();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 关闭文件资源
     */
    private static void closeResources() {
        try {
            if (channel != null) {
                channel.close();
            }
            if (file != null) {
                file.close();
            }
        } catch (Exception e) {
            // 忽略关闭异常
        }
    }
}
