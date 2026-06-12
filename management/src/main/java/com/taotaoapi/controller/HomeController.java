package com.taotaoapi.controller;


import com.taotaoapi.home.ArticleRequest;
import com.taotaoapi.home.CategoryResponse;
import com.taotaoapi.home.CountryResponse;
import com.taotaoapi.home.TagsResponse;
import com.taotaoapi.response.ApiResponse;
import com.taotaoapi.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/article")
    public ApiResponse createArticle(@RequestBody ArticleRequest req, @AuthenticationPrincipal UserDetails userDetails) {
        homeService.postArticle(req,userDetails.getUsername());
        return ApiResponse.success("新增成功",null);
    }
}
