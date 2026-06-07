package com.taotaoapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotaoapi.mapper.UserMapper;
import com.taotaoapi.response.ApiResponse;
import com.taotaoapi.service.JwtService;
import com.taotaoapi.service.RedisTokenService;
import com.taotaoapi.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final RedisTokenService redisTokenService;
  private final UserMapper userMapper;

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    /**
     * 白名單：登入 / 註冊 API 不需要 JWT 驗證
     * 直接放行，不做任何處理
     */
    if (request.getServletPath().contains("/taotao/auth")) {
      filterChain.doFilter(request, response);
      return;
    }

    /**
     * 從 HTTP Header 取出 JWT
     * 格式：Authorization: Bearer xxx.yyy.zzz
     */
    final String authHeader = request.getHeader("Authorization");

    final String jwt;
    final String userEmail;

    /**
     * 沒有 token 或格式錯誤 → 直接放行（當作未登入）
     */
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    /**
     * 把 "Bearer " 去掉，只留下 JWT 本體
     */
    jwt = authHeader.substring(7);

    /**
     * 從 JWT 裡解析使用者帳號（email / username）
     * 👉 JWT 本身就包含 user identity
     */
    userEmail = jwtService.extractUsername(jwt);

    /**
     * 確保：
     * - 有 userEmail
     * - 目前還沒有登入（避免重複設定）
     */
    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      /**
       * 去資料庫撈使用者資料
       * 包含：
       * - 帳號
       * - 密碼（加密）
       * - 權限（ROLE / AUTHORITY）
       */
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

      /**
       * 額外做「Token 是否還有效」檢查
       * （例如：是否被登出 / 是否被封鎖）
       */
      // JWT 驗證
      User user = userMapper.findByEmail(userEmail);
      Integer userId = user.getId();
      boolean jwtValid = jwtService.isTokenValid(jwt, userDetails);

      // Redis 驗證
      String redisAccessToken = redisTokenService.getAccessToken(userId);
      System.out.println("jwtValid = " + jwtValid);
      boolean redisValid = jwt.equals(redisAccessToken);
      System.out.println("redisValid = " + redisValid);


      // token 無效 → 9004
      if (!jwtValid || !redisValid) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setCode("9004");
        res.setMsg("Token 已過期或失效");
        res.setData(null);

        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(
                new ObjectMapper().writeValueAsString(res)
        );
        return;
      }
      /**
       * 最終驗證：
       * - JWT 簽章正確
       * - JWT 沒過期
       * - DB token 沒被撤銷
       */
        /**
         * 建立「登入身份物件」，代表：這個 request 已經是登入狀態
         */
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,        // 使用者身份
                        null,               // 密碼（JWT 不需要）
                        userDetails.getAuthorities() // 權限
                );

        /**
         * 把 request 資訊放進 authentication（例如 IP、session info）
         */
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        /**
         * 把「登入狀態」存進 Spring Security，之後 Controller / Service 都可以拿到這個 user
         */
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    /**
     * 放行 request，繼續往 Controller 走
     */
    filterChain.doFilter(request, response);
  }
}

