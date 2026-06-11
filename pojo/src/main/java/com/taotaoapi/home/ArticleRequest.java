package com.taotaoapi.home;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRequest {
    private Long userId;
    private String countryId;
    private Long categoryId;

    private String title;
    private String content;

    private List<String> imageUrls;
    private List<Long> tags;
}