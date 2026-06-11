package com.taotaoapi.config.upload;

import com.taotaoapi.cloudflare.CloudflareProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class CloudflareR2Config {
    // R2 設定來源（yml / env）
    private final CloudflareProperties cloudflareProperties;
    // 建立全域 S3Client（其實是 R2 client）
    @Bean
    public S3Client s3Client() {
        // R2 相容設定
        S3Configuration serviceConfig = S3Configuration.builder()
                .pathStyleAccessEnabled(true)   // R2 必須開
                .chunkedEncodingEnabled(false)   // 避免 AWS chunk upload
                .build();
        // 建立並回傳 S3Client
        return S3Client.builder()
                // HTTP client（底層傳輸）
                .httpClientBuilder(ApacheHttpClient.builder())
                // R2 不使用 region，但 SDK 必填
                .region(Region.of("auto"))
                // 指向 Cloudflare R2 endpoint
                .endpointOverride(URI.create(cloudflareProperties.getEndpoint()))
                // 設定金鑰（重點）
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        cloudflareProperties.getAccessKey(),
                                        cloudflareProperties.getSecretKey()
                                )
                        )
                )
                // 套用 R2 設定
                .serviceConfiguration(serviceConfig)
                // 建立 client
                .build();
    }
    @Bean
    public S3Presigner s3Presigner() {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                cloudflareProperties.getAccessKey(),
                cloudflareProperties.getSecretKey()
        );

        return S3Presigner.builder()
                .endpointOverride(URI.create(cloudflareProperties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of("auto"))
                .build();
    }
}