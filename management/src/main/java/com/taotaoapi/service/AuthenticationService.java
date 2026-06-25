package com.taotaoapi.service;
import com.taotaoapi.auth.AuthenticationRequest;
import com.taotaoapi.auth.AuthenticationResponse;
import com.taotaoapi.auth.RegisterRequest;
import com.taotaoapi.exception.BusinessException;
import com.taotaoapi.mapper.UserMapper;
import com.taotaoapi.home.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final JwtService jwtService;
  private final RedisTokenService redisTokenService;
  private final UserMapper userMapper;
  // 密碼加密工具 (BCrypt)，Spring Security 提供的介面（Interface）
  private final PasswordEncoder passwordEncoder;
  // Spring Security 身分驗證器
  private final AuthenticationManager authenticationManager;
  /**
   * 使用者註冊
   */
  @Transactional
  public AuthenticationResponse register(RegisterRequest request) {

    User findUser = userMapper.findByEmail(request.getEmail());

    if (findUser != null) {
      throw new BusinessException(401, "電子郵件已存在");
    }

    User user = new User();
    user.setFirstname(request.getFirstname());
    user.setLastname(request.getLastname());
    user.setNickname(request.getNickname());
    user.setEmail(request.getEmail());
    user.setPhone(request.getPhone());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());

    userMapper.insertUser(user);

    String accessToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    long expiredDate = System.currentTimeMillis() + jwtService.getJwtExpiration();
    long expiredDateR = System.currentTimeMillis() + jwtService.getRefreshExpiration();

    redisTokenService.saveAccessToken(user.getId(), accessToken, 15 * 60);
    redisTokenService.saveRefreshToken(user.getId(), refreshToken, 7 * 24 * 60 * 60);

    return AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiredDate(expiredDate)
            .expiredDateR(expiredDateR)
            .build();
  }
  /**
   * 使用者登入
   */
  @Transactional
  public AuthenticationResponse authenticate(AuthenticationRequest request) {

    try {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
      );
    } catch (AuthenticationException e) {
      throw new BusinessException(401, "帳號或密碼錯誤");
    }

    User user = userMapper.findByEmail(request.getEmail());

    String accessToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    long expiredDate = System.currentTimeMillis() + jwtService.getJwtExpiration();
    long expiredDateR = System.currentTimeMillis() + jwtService.getRefreshExpiration();

    // login = 踢掉舊 session
    redisTokenService.revokeUser(user.getId());

    redisTokenService.saveAccessToken(user.getId(), accessToken, 15 * 60);
    redisTokenService.saveRefreshToken(user.getId(), refreshToken, 7 * 24 * 60 * 60);


    return AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiredDate(expiredDate)
            .expiredDateR(expiredDateR)
            .build();
  }

  /**
   * Refresh Token 換取新的 Access Token
   */
  public AuthenticationResponse refreshToken(String refreshToken) {
    String email = jwtService.extractUsername(refreshToken);
    User user = userMapper.findByEmail(email);

    String storedRefresh = redisTokenService.getRefreshToken(user.getId());

    if (storedRefresh == null || !storedRefresh.equals(refreshToken)) {
      throw new BusinessException(400, "Refresh Token 已失效");
    }

    if (!jwtService.isTokenValid(refreshToken, user)) {
      throw new BusinessException(400, "Token 無效");
    }

    String newAccessToken = jwtService.generateToken(user);
    redisTokenService.saveAccessToken(user.getId(), newAccessToken, 15 * 60);
    long expiredDate = System.currentTimeMillis() + jwtService.getJwtExpiration();
    long expiredDateR = System.currentTimeMillis() + jwtService.getRefreshExpiration();

    return AuthenticationResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken)
            .expiredDate(expiredDate)
            .expiredDateR(expiredDateR)
            .build();
  }
}
