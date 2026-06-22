package com.taotaoapi.home.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long id;
    private Long articleId;
    private boolean canDelete;
    private Integer userId;
    private String username;
    private String content;
    private long totalLikes;
    private boolean isLiked;
}