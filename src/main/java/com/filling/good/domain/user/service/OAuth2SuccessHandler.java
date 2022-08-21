package com.filling.good.domain.user.service;

import com.filling.good.domain.user.entity.User;
import com.filling.good.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.filling.good.domain.user.enumerate.AuthProvider.GOOGLE;
import static com.filling.good.domain.user.enumerate.Job.BLANK;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = (String) oAuth2User.getAttributes().get("email");
        String name = (String) oAuth2User.getAttributes().get("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> oauthJoin(email, name));

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getAuthProvider());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getAuthProvider());

        String targetUrl = UriComponentsBuilder.fromUriString("/auth/login/google")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private User oauthJoin(String email, String name) {
        String autoCreatedNickname = ""; //닉네임 자동 생성
        do {
            autoCreatedNickname = RandomStringUtils.random(15, true, true);
        } while (userRepository.existsByNickname(autoCreatedNickname));

        User user = new User(
                email,
                "",
                autoCreatedNickname,
                name,
                BLANK,
                GOOGLE
        );
        return userRepository.save(user);
    }

}
