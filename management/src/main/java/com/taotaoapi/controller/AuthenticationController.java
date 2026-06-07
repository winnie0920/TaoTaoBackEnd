package com.taotaoapi.controller;

import com.taotaoapi.response.ApiResponse;
import com.taotaoapi.auth.AuthenticationRequest;
import com.taotaoapi.auth.AuthenticationResponse;
import com.taotaoapi.auth.RegisterRequest;
import com.taotaoapi.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/taotao/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  // 驗證相關邏輯
  private final AuthenticationService service;
  /**
   * 註冊帳號
   */
  @PostMapping("/register")
  public  ApiResponse<AuthenticationResponse> register(
          @RequestBody RegisterRequest request
  ) {
    return ApiResponse.success("註冊成功", service.register(request));
  }

  /**
   * 使用者登入
   */
  @PostMapping("/authenticate")
  public  ApiResponse<AuthenticationResponse> authenticate(
          @RequestBody AuthenticationRequest request
  ) {
    return ApiResponse.success("登入成功", service.authenticate(request));
  }

  /**
   * 更新 Access Token
   */
  @PostMapping("/refresh-token")
  public ApiResponse<AuthenticationResponse> refreshToken(
          @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    return ApiResponse.success("刷新成功", service.refreshToken(token));
  }
}