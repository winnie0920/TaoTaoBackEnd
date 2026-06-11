package com.taotaoapi.controller;


import com.taotaoapi.home.ArticleRequest;
import com.taotaoapi.home.CategoryResponse;
import com.taotaoapi.home.CountryResponse;
import com.taotaoapi.response.ApiResponse;
import com.taotaoapi.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;

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

    @PostMapping("/article")
    public ApiResponse createArticle(@RequestBody ArticleRequest req) {
        homeService.postArticle(req);
        return ApiResponse.success("新增成功",null);
    }
}
