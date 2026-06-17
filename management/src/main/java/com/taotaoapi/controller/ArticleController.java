package com.taotaoapi.controller;

import com.taotaoapi.home.article.ArticleList;
import com.taotaoapi.home.article.ArticleQuery;
import com.taotaoapi.home.article.ArticleRequest;
import com.taotaoapi.home.article.Page;
import com.taotaoapi.response.ApiResponse;
import com.taotaoapi.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/taotao/article")
public class ArticleController {

    private final ArticleService articleService;
    @PostMapping
    public ApiResponse<Void> createArticle(@RequestBody ArticleRequest req, @AuthenticationPrincipal UserDetails userDetails) {
        articleService.postArticle(req,userDetails.getUsername());
        return ApiResponse.success("新增成功",null);
    }

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
    @PostMapping("/like")
    public ApiResponse<Void> postArticleLike(
            @RequestParam Long articleId,
            @AuthenticationPrincipal UserDetails userDetails // 👈 跟你之前一樣，從 Token 拿 email
    ) {
        String result = articleService.postArticleLike(articleId, userDetails.getUsername());

        return ApiResponse.success(result, null);
    }
}
