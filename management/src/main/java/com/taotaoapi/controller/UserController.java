package com.taotaoapi.controller;

import com.taotaoapi.home.article.ArticleList;
import com.taotaoapi.home.article.ArticleQuery;
import com.taotaoapi.home.article.Page;
import com.taotaoapi.home.user.UserDTO;
import com.taotaoapi.home.user.UserUpdateDTO;
import com.taotaoapi.response.ApiResponse;
import com.taotaoapi.service.ArticleService;
import com.taotaoapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/taotao/user")
public class UserController {

    private final UserService userService;
    // 取得使用者發佈的文章
    @GetMapping
    public ApiResponse<Page<ArticleList>> getUserArticle(
            ArticleQuery query,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        return ApiResponse.success(
                "查詢成功",
                userService.getUserArticle(
                        query,
                        userDetails.getUsername()
                )
        );
    }

    // 取得個人主頁資訊
    @GetMapping("/dashboard")
    public ApiResponse<UserDTO> getUserDashboard(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success(
                "查詢成功",
                userService.getUserDashboard(userDetails.getUsername())
        );
    }
    // 更新個人主頁資訊
    @PostMapping("/dashboard/update")
    public ApiResponse<Void> updateUserDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserUpdateDTO updateDTO
    ) {
        userService.updateUserDashboard(userDetails.getUsername(), updateDTO);
        return ApiResponse.success("更新成功", null);
    }

}
