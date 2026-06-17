package com.taotaoapi.home.article;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// 取得文章格式
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleList {
    private Long id;
    private Long userId;
    private String userName;

    private String title;
    private String content;
    private Long countryId;
    private String countryName;
    private String countryIcon;

    private Long categoryId;
    private String categoryName;
    private String categoryIcon;

    private List<ArticleImage> images;
    private List<ArticleTag> tags;

    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;

    private Boolean liked;
    private Boolean favorited;

    private LocalDateTime createdTime;
}

