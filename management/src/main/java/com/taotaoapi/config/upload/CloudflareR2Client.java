package com.taotaoapi.config.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class CloudflareR2Client {

    private final S3Client s3Client;

    /**
     * 上傳檔案（永久）
     */
    public String uploadFile(String bucket, String key, InputStream inputStream, long size, String contentType) {

        s3Client.putObject(
                b -> b.bucket(bucket).key(key).contentType(contentType),
                software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, size)
        );
        return key;
    }

    /**
     * 檢查檔案是否存在
     */
    public boolean exists(String bucket, String key) {
        try {
            s3Client.headObject(b -> b.bucket(bucket).key(key));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刪除檔案（可選）
     */
    public void deleteFile(String bucket, String key) {
        s3Client.deleteObject(b -> b.bucket(bucket).key(key));
    }
}