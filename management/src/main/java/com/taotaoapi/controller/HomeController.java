package com.taotaoapi.controller;

import com.taotaoapi.home.CountryResponse;
import com.taotaoapi.response.ApiResponse;
import com.taotaoapi.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
