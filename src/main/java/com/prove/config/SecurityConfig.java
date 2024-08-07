package com.prove.config;


import com.prove.domain.JWT.jwt.CustomLogoutFilter;
import com.prove.domain.JWT.jwt.JWTFilter;
import com.prove.domain.JWT.jwt.JWTUtil;
import com.prove.domain.JWT.jwt.LoginFilter;
import com.prove.domain.JWT.repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    private final RefreshRepository refreshRepository;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,RefreshRepository refreshRepository) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.refreshRepository = refreshRepository;
        this.jwtUtil = jwtUtil;
    }


    //우리는 시큐리티를 통해서 회원정보 저장 조회 할때는 비밀번호를 항상 캐시로 암호화시켜서 검증해서 진행

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        //Cors 설정
        http
                .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);
                        configuration.setExposedHeaders(Collections.singletonList("Access"));

                        return configuration;
                    }
                })));

        //우리는 시큐리티를 통해서 회원정보 저장 조회 할때는 비밀번호를 항상 캐시로 암호화시켜서 검증해서 진행

        //csrf disable session에서는 session이 고정 csrf공격 방어 필수 jwt는 session이 stateless상태 방어 필요
        //없어서 disable
        http
                .csrf(csrf->csrf.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //경로별 인가 작업
        //로그인 루트 조인은 모두 허용
        ///admin은 admin만 접근가능
        //나머지 모든 요청들은 로그인을 해야만 접근 가능하게 authenticated설정을 해준다.
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/join").permitAll()
                        .requestMatchers("reissue").permitAll()
                        .requestMatchers("/api/allProves").permitAll()
                        .requestMatchers("/api/allProves/v2/{tags}").permitAll()
                        .requestMatchers("/api/allProves/v3/{tags}").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());

        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtUtil,refreshRepository), UsernamePasswordAuthenticationFilter.class);
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        //세션 설정
        //jwt에서는 항상 세션을 stateless로 관리 한다.
        //가장 중요하다.
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}
