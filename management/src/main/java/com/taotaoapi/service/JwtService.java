package com.taotaoapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Getter
public class JwtService {

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;
  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;
  @Value("${application.security.jwt.reset.expiration}")
  private long resetExpiration;

  /**
   * 從 JWT 中取出使用者名稱(email)
   * JWT:
   * {
   *   "sub":"test@gmail.com"
   * }
   */
  public String extractUsername(String token) {
    // interface「一定要有實作物件(new)才能用」
    return extractClaim(token, Claims::getSubject);
  }


  /**
   * 泛型方法
   * 可以從 JWT Claims 中取出任何欄位
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }


  /**
   * 產生 Access Token
   * 不帶額外 Claims
   */
  public String generateToken(
          UserDetails userDetails
  ) {
    return generateToken(
            new HashMap<>(),
            userDetails
    );
  }


  /**
   * 產生 Access Token
   * 可自行加入額外資訊，例如：role、userId
   */
  public String generateToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails
  ) {
    return buildToken(
            extraClaims,
            userDetails.getUsername(),
            jwtExpiration
    );
  }


  /**
   * 產生 Refresh Token
   */
  public String generateRefreshToken(
          UserDetails userDetails
  ) {
    return buildToken(
            new HashMap<>(),
            userDetails.getUsername(),
            refreshExpiration
    );
  }


  public String generateResetToken(String email) {

    Map<String, Object> claims = new HashMap<>();
    claims.put("type", "RESET_PASSWORD");

    return buildToken(
            claims,
            email,
            jwtExpiration
    );
  }

  /**
   * 真正建立 JWT 的地方
   *
   * JWT 結構：
   * Header
   * Payload
   * Signature
   */
  private String buildToken(
          Map<String, Object> extraClaims,
          String subject,
          long expiration
  ) {
    return Jwts.builder()
            // 自訂 Payload
            .setClaims(extraClaims)
            // subject (通常放 email)
            .setSubject(subject)
            // 建立時間
            .setIssuedAt(new Date(System.currentTimeMillis())
            )
            // 過期時間
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            // 使用 HS256 簽章
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            // 轉成 JWT 字串
            .compact();
  }

  /**
   * 驗證 JWT 是否有效
   *
   * 驗證：
   * 1. 使用者是否一致
   * 2. Token 是否過期
   */
  public boolean isTokenValid(
          String token,
          UserDetails userDetails
  ) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }


  /**
   * 判斷 Token 是否過期
   */
  private boolean isTokenExpired(String token) {
  return extractExpiration(token).before(new Date());
  }


  /**
   * 取得 JWT 過期時間
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }


  /**
   * 解析 JWT
   * 驗證簽章成功後
   * 取得 Payload(Claims)
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
            // 使用密鑰驗證 JWT
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            // 取得 Payload
            .getBody();
  }

  /**
   * 將 Base64 Secret Key
   * 轉成 Java Key 物件
   * JWT 簽章與驗證都會使用
   */
  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}