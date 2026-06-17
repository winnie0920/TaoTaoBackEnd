package com.taotaoapi.home.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 儲存資料庫article格式
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    private Long id;

    private Long userId;
    private Long countryId;
    private Long categoryId;

    private String title;
    private String content;
}