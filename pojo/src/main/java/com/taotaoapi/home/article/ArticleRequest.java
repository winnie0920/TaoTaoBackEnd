package com.taotaoapi.home.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 新增文章格式
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRequest {
    private Long userId;
    private Long countryId;
    private Long categoryId;

    private String title;
    private String content;

    private List<String> imageUrls;
    private List<Long> tags;
}