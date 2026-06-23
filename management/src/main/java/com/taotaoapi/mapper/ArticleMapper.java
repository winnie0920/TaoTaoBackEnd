package com.taotaoapi.mapper;

import com.taotaoapi.home.Image;
import com.taotaoapi.home.article.*;
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


    // 取得收藏文章列表
    List<ArticleList> selectFavoriteArticles(
            @Param("query") ArticleQuery query,
            @Param("userId") Long userId
    );

    // 計算該使用者的收藏總數
    Long countFavoriteArticles(
            @Param("userId") Long userId
    );

    // 新增收藏紀錄
    void insertFavorite(
            @Param("userId") Long userId,
            @Param("articleId") Long articleId
    );

    // 刪除收藏紀錄
    void deleteFavorite(
            @Param("userId") Long userId,
            @Param("articleId") Long articleId
    );

    // 檢查是否收藏過（用來防止重複收藏）
    int checkFavoriteExists(
            @Param("userId") Long userId,
            @Param("articleId") Long articleId
    );


    // 查詢文章按讚、收藏、留言
    ArticleStatus selectArticleStatus(
            @Param("articleId") Long articleId,
            @Param("userId") Long userId
    );

    // 查詢標籤
    List<ArticleTag> selectTagsByArticleId(@Param("id") Long id);

    // 取得留言
    List<Comment> selectComments(@Param("articleId") Long articleId, @Param("userId") Integer userId);

    // 新增留言
    void insertComment(Comment comment);

    // 刪除留言
    void deleteComment(Long id);

    // 檢查是否為本人、留過言
    Comment selectCommentById(@Param("id") Long id, @Param("userId") Integer userId);

    // 刪除留言總數量
    Integer countCommentsByArticleId(Long articleId);

    // 刪除讚數
    void deleteCommentLikesByCommentId(@Param("commentId") Long commentId);

    // 檢查用戶是否已按讚
    int checkCommentLikeExists(@Param("commentId") Long commentId, @Param("userId") Integer userId);

    // 新增留言讚
    void insertCommentLike(@Param("commentId") Long commentId, @Param("userId") Integer userId);

    // 取消留言讚
    void deleteCommentLike(@Param("commentId") Long commentId, @Param("userId") Integer userId);

    // 統計該留言的讚數
    int countCommentLikes(@Param("commentId") Long commentId);

    // 撈取指定文章內的所有圖片資料
    List<ArticleImage> selectImagesByArticleId(Long articleId);

}
