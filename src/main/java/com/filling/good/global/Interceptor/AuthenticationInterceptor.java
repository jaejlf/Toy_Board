package com.filling.good.global.Interceptor;

import com.filling.good.domain.user.exception.CustomJwtException;
import com.filling.good.domain.user.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    //컨트롤러 실행 전 수행 (true -> 컨트롤러로 진입, false -> 진입 X)
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = jwtTokenProvider.resolveToken(request);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new CustomJwtException();
        }

        return true;
    }
}
