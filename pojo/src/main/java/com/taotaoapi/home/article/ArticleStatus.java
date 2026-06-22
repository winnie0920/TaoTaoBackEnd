package com.taotaoapi.home.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用於「狀態校準」場景，僅包含點讚、收藏、留言等動態計數與狀態，
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleStatus {
    private Long id;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private Boolean liked;
    private Boolean favorited;
}
