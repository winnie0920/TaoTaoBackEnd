package com.taotaoapi.home;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    private Long articleId;
    private String imageUrl;
    private Integer sortOrder;
    private Boolean isCover;
}

