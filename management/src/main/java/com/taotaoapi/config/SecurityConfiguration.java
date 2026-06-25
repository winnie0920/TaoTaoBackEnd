package com.taotaoapi.config;

import com.taotaoapi.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.List;
import static com.taotaoapi.home.Role.ADMIN;
import static com.taotaoapi.home.Role.MANAGER;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Slf4j
@Configuration // 表示這是一個「設定類」，Spring 會在啟動時讀取
@EnableWebSecurity // 啟用 Spring Security 安全機制
@EnableMethodSecurity // 開啟方法層級的權限控制（例如 @PreAuthorize）
@RequiredArgsConstructor

public class SecurityConfiguration {
    private final ObjectMapper objectMapper;
    /**
     * 白名單 URL：
     * 這些路徑「不需要登入」就可以直接存取
     */
    private static final String[] WHITE_LIST_URL = {
            "/taotao/auth/**",

            // Swagger 文件相關（API 文件工具）
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    // JWT 過濾器
    private final JwtAuthenticationFilter jwtAuthFilter;
    // Spring Security 用來做「帳號密碼驗證」的核心邏輯
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /**
         * 關閉 CSRF 防護
         * 因為現在是 JWT + Stateless API，不使用 session
         */
        http.csrf(AbstractHttpConfigurer::disable).cors(cors -> cors.configurationSource(corsConfigurationSource()))
                //設定「請求授權規則」
                .authorizeHttpRequests(req -> req
                         //白名單：不用登入
                        .requestMatchers(WHITE_LIST_URL).permitAll()
                        /**
                         * 管理功能 API（管理後台）：
                         * 需要 MANAGER 或 ADMIN 角色才能存取
                         */
                        .requestMatchers("/taotao/management/**")
                        .hasAnyRole(ADMIN.name(), MANAGER.name())
                        /**
                         * 系統級 API（高權限操作）：
                         * 只允許 ADMIN 角色存取
                         */
                        .requestMatchers("/taotao/system/**")
                        .hasRole("ADMIN")
                        /**
                         * 其他所有 API：
                         * 一律需要登入（JWT 驗證成功）
                         */
                        .anyRequest()
                        .authenticated()
                )
                /**
                 * Session 管理策略：
                 * STATELESS = 不使用 session（JWT 常見設定）
                 */
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))

                /**
                 * 指定 AuthenticationProvider：
                 * Spring 用它來處理「登入驗證邏輯」
                 */
                .authenticationProvider(authenticationProvider)

                /**
                 * 加入 JWT 過濾器
                 * 在 UsernamePasswordAuthenticationFilter 之前執行
                 * → 先解析 token，再讓 Spring Security 判斷是否登入
                 */
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                /**
                 * 登出設定
                 */
                .logout(logout ->
                        logout.logoutUrl("/taotao/auth/logout") // 登出 API
                                //登出時要做的事情（例如清 token）
                                .addLogoutHandler(logoutHandler)
                                // 登出成功後，清除 Spring Security 的認證資訊
                                .logoutSuccessHandler((request, response, authentication) -> {
                                    SecurityContextHolder.clearContext();
                                    response.setContentType("application/json;charset=UTF-8");
                                    response.getWriter().write(
                                            objectMapper.writeValueAsString(
                                                    ApiResponse.success("登出成功", null)
                                            )
                                    );
                                })
                );

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "https://winnie0920.github.io",
                "http://100.118.4.50:*",                 // 允許你的 Tailscale 內網 IP 的任何 Port
                "https://weiwei-pc.tailb205c1.ts.net"    // 如果之後要用 Funnel 也可以直接通
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}