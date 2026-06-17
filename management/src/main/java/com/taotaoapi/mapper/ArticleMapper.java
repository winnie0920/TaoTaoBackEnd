package com.taotaoapi.mapper;

import com.taotaoapi.home.Image;
import com.taotaoapi.home.article.Article;
import com.taotaoapi.home.article.ArticleList;
import com.taotaoapi.home.article.ArticleQuery;
import com.taotaoapi.home.article.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {
    void insertArticle(Article article);

    void insertArticleTag(
            @Param("articleId") Long articleId,
            @Param("tagId") Long tagId
    );

    void insertArticleImage(Image image);

    List<ArticleList> selectArticleList(
            @Param("query") ArticleQuery query,
            @Param("userId") Long userId
    );
    Long countArticles(ArticleQuery query);

    // 新增點讚紀錄
    int insertLike(
            @Param("userId") Long userId,
            @Param("articleId") Long articleId
    );

    // 刪除點讚紀錄
    int deleteLike(
            @Param("userId") Long userId,
            @Param("articleId") Long articleId
    );

    // 檢查是否已經點讚過（用來防止重複點讚）
    int checkExists(
            @Param("userId") Long userId,
            @Param("articleId") Long articleId
    );

    // 查詢標籤
    List<ArticleTag> selectTagsByArticleId(@Param("id") Long id);
}
