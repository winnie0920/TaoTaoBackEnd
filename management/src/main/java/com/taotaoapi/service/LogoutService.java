package com.taotaoapi.service;

import com.taotaoapi.mapper.UserMapper;
import com.taotaoapi.home.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final RedisTokenService redisTokenService;
  private final JwtService jwtService;
  private final UserMapper userMapper;

  @Override
  public void logout(HttpServletRequest request,
                     HttpServletResponse response,
                     Authentication authentication) {

    String authHeader = request.getHeader("Authorization");

    // 沒 token 直接結束
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }

    String jwt = authHeader.substring(7);
    String email = jwtService.extractUsername(jwt);

    if (email == null) {
      return;
    }
    User user = userMapper.findByEmail(email);

    if (user != null) {
      redisTokenService.revokeUser(user.getId());
    }
  }
}