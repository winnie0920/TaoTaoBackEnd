package com.taotaoapi.service;

import com.taotaoapi.cloudflare.CloudflareProperties;
import com.taotaoapi.config.upload.CloudflareR2Client;
import com.taotaoapi.config.upload.FileNameUtil;
import com.taotaoapi.home.article.ArticleImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final S3Client s3Client;
    private final CloudflareProperties props;
    private final CloudflareR2Client r2Client;

    public String upload(MultipartFile file) {

        String originalName = file.getOriginalFilename();
        String folder = "taotao/";

        // 1. 產生 base key
        String key = folder + FileNameUtil.baseKey(originalName);
        int index = 0;

        try {
            // 2. 去重
            while (r2Client.exists(props.getBucket(), key)) {
                index++;
                key = folder + FileNameUtil.addIndex(originalName, index);
            }
            // 3. 上傳到 R2
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(props.getBucket())
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
            return props.getBaseUrl() + key;
        } catch (Exception e) {
            throw new RuntimeException("upload failed", e);
        }
    }

    // 統一入口（單張 + 多張）
    public List<String> uploadFiles(List<MultipartFile> files) {

        List<String> result = new ArrayList<>();

        if (files == null || files.isEmpty()) {
            return result;
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            result.add(upload(file)); // reuse 原本 upload
        }

        return result;
    }

    public void deleteByUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {
            // 1. 將網址轉為 URI 物件，方便提取路徑
            java.net.URI uri = new java.net.URI(imageUrl);
            String path = uri.getPath(); // 例如 "/theme/image_1.png" 或 "/image_1.png"

            // 2. 移除最前面的斜線 "/"，得到真正的 S3 Key
            String key = path.startsWith("/") ? path.substring(1) : path;

            // 3. 執行刪除
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(key)
                    .build());

            log.info("成功從 R2 刪除檔案, key: {}", key);
        } catch (Exception e) {
            log.error("從 R2 刪除檔案失敗, url: {}", imageUrl, e);
            throw new RuntimeException("delete failed", e);
        }
    }
}