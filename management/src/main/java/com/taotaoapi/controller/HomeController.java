package com.taotaoapi.controller;


import com.taotaoapi.home.*;
import com.taotaoapi.home.article.ArticleList;
import com.taotaoapi.home.article.ArticleQuery;
import com.taotaoapi.home.article.ArticleRequest;
import com.taotaoapi.home.article.Page;
import com.taotaoapi.home.country.CountryResponse;
import com.taotaoapi.response.ApiResponse;
import com.taotaoapi.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/taotao")
public class HomeController {

    private final HomeService homeService;
    @GetMapping("/countries")
    public ApiResponse<List<CountryResponse>> getCountries() {
        return ApiResponse.success(
                "取得國家列表成功",
                homeService.getCountries()
        );
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> getCategories() {
        return ApiResponse.success(
                "取得分類列表成功",
                homeService.getCategories()
        );
    }

    @GetMapping("/tags")
    public ApiResponse<List<TagsResponse>> getTags() {
        return ApiResponse.success(
                "取得分類列表成功",
                homeService.getTags()
        );
    }


}
