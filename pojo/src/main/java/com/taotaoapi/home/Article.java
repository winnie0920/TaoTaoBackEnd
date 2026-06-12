package com.taotaoapi.home;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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