package com.filling.good.domain.user.service;

import com.filling.good.domain.user.enumerate.AuthProvider;
import com.filling.good.domain.user.exception.InvalidTokenException;
import com.filling.good.global.service.RedisService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access.token.valid.time}")
    private long accessTokenValidTime;

    @Value("${jwt.refresh.token.valid.time}")
    private long refreshTokenValidTime;

    private final UserDetailsService userDetailsService;
    private final RedisService redisService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAccessToken(String email, AuthProvider authProvider) {
        return createToken(email, authProvider, accessTokenValidTime);
    }

    public String createRefreshToken(String email, AuthProvider authProvider) {
        String refreshToken = createToken(email, authProvider, refreshTokenValidTime);
        redisService.setValues(email, refreshToken, Duration.ofMillis(refreshTokenValidTime));
        return refreshToken;
    }

    public String createToken(String email, AuthProvider authProvider, Long tokenValidTime) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("provider", authProvider);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getPayload(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getPayload(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public Long calValidTime(String jwtToken) {
        Date now = new Date();
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
        return claims.getBody().getExpiration().getTime() - now.getTime();
    }

}