package com.taotaoapi.cloudflare;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloudflare.r2")
public class CloudflareProperties {
    private String endpoint;
    private String baseUrl;
    private String accessKey;
    private String secretKey;
    private String bucket;
}