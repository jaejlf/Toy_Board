package com.filling.good.global.config;

import com.filling.good.domain.user.config.JwtFilter;
import com.filling.good.domain.user.service.JwtTokenProvider;
import com.filling.good.domain.user.service.OAuth2Service;
import com.filling.good.domain.user.service.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2Service oAuth2Service;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(new JwtFilter(jwtTokenProvider), OAuth2LoginAuthenticationFilter.class)
                .oauth2Login()
                .successHandler(oAuth2SuccessHandler)
                .userInfoEndpoint().userService(oAuth2Service);

        http
                .addFilterBefore(new JwtFilter(jwtTokenProvider),UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}