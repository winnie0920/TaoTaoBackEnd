package com.taotaoapi.controller;

import com.taotaoapi.home.article.*;
import com.taotaoapi.response.ApiResponse;
import com.taotaoapi.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/taotao/article")
public class ArticleController {

    private final ArticleService articleService;
    // 新增文章
    @PostMapping
    public ApiResponse<Void> postArticle(@RequestBody ArticleRequest req, @AuthenticationPrincipal UserDetails userDetails) {
        articleService.postArticle(req,userDetails.getUsername());
        return ApiResponse.success("新增成功",null);
    }

    // 取得文章
    @GetMapping
    public ApiResponse<Page<ArticleList>> getArticleList(
            ArticleQuery query,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        return ApiResponse.success(
                "查詢成功",
                articleService.getArticleList(
                        query,
                        userDetails.getUsername()
                )
        );
    }
    // 新增、取消按讚
    @PostMapping("/like")
    public ApiResponse<ArticleStatus> postArticleLike(
            @RequestParam Long articleId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ArticleStatus status = articleService.postArticleLike(articleId, userDetails.getUsername());
        // 將 status 封裝進 ApiResponse 回傳給前端
        return ApiResponse.success("新增、取消成功", status);
    }

    // 新增、取消收藏
    @PostMapping("/favorite")
    public ApiResponse<ArticleStatus> postArticleFavorite(
            @RequestParam Long articleId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ArticleStatus status = articleService.postArticleFavorite(articleId, userDetails.getUsername());
        return ApiResponse.success("新增、取消成功", status);
    }

    // 取得留言
    @GetMapping("/comment")
    public ApiResponse<List<Comment>> getComments(
            @RequestParam Long articleId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success(
                "查詢成功",
                articleService.getComments(articleId, userDetails.getUsername())
        );
    }
    // 新增留言
    @PostMapping("/comment")
    public ApiResponse<Integer> postComment(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long articleId = Long.valueOf(body.get("articleId").toString());
        String content = body.get("content").toString();
        Integer newCount = articleService.postComment(articleId, userDetails.getUsername(), content);
        return ApiResponse.success("留言成功", newCount);
    }

    // 刪除留言
    @DeleteMapping("/comment/{id}")
    public ApiResponse<Integer> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 接收回傳的最新總數
        Integer remainingCount = articleService.deleteComment(id, userDetails.getUsername());
        return ApiResponse.success("刪除成功", remainingCount);
    }

    // 新增、取消留言讚
    @PostMapping("/comment/like")
    public ApiResponse<Integer> postCommentLike(
            @RequestParam Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer newLikeCount = articleService.postCommentLike(id, userDetails.getUsername());
        return ApiResponse.success("新增、取消成功", newLikeCount);
    }
}
