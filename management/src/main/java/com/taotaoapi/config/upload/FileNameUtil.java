package com.taotaoapi.config.upload;
public class FileNameUtil {
    // 原始檔名
    public static String baseKey(String originalName) {
        return originalName;
    }
    // 加 (1)(2)(3)
    public static String addIndex(String baseName, int index) {
        int dotIndex = baseName.lastIndexOf(".");
        String name = baseName.substring(0, dotIndex);
        String ext = baseName.substring(dotIndex);
        return name + "(" + index + ")" + ext;
    }
}