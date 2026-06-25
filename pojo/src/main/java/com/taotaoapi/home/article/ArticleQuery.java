package com.taotaoapi.home.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 預設頁數
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleQuery {
    private String keyword;
    private Integer categoryId;
    private Integer countryId;
    private Integer authorId;
    // 上一頁最後一筆 ID
    private Long lastId;
    private Integer size = 10;
}


