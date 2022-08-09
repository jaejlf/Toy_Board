package com.filling.good.domain.user.service;

import com.filling.good.domain.user.dto.request.LoginRequest;
import com.filling.good.domain.user.dto.request.SignUpRequest;
import com.filling.good.domain.user.dto.request.TokenRequest;
import com.filling.good.domain.user.dto.response.AuthUserResponse;
import com.filling.good.domain.user.dto.response.UserResponse;
import com.filling.good.domain.user.entity.User;
import com.filling.good.domain.user.enumerate.Job;
import com.filling.good.domain.user.exception.DuplicateUserException;
import com.filling.good.domain.user.exception.ExpiredRefreshTokenException;
import com.filling.good.domain.user.exception.PasswordErrorException;
import com.filling.good.domain.user.exception.UserNotFoundException;
import com.filling.good.domain.user.repository.UserRepository;
import com.filling.good.global.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.filling.good.domain.user.enumerate.AuthProvider.DEFAULT;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(rollbackOn = {Exception.class})
    public UserResponse join(SignUpRequest signUpRequest) {
        validateDuplicateUser(signUpRequest);
        User user = new User(
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getNickname(),
                Job.findJobCode(signUpRequest.getJobValue()),
                DEFAULT
        );

        return UserResponse.of(userRepository.save(user));
    }

    @Transactional(rollbackOn = {Exception.class})
    public AuthUserResponse login(LoginRequest loginRequest) {
        User user = getCheckedUser(loginRequest.getEmail());
        checkPassword(loginRequest, user);

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        return AuthUserResponse.of(user, accessToken, refreshToken);
    }

    @Transactional(rollbackOn = {Exception.class})
    public AuthUserResponse tokenReIssue(TokenRequest tokenRequest) {
        User user = getCheckedUser(tokenRequest.getEmail());
        String refreshToken = validateRefreshToken(tokenRequest);
        String accessToken = jwtTokenProvider.createAccessToken(tokenRequest.getEmail());

        return AuthUserResponse.of(user, accessToken, refreshToken);
    }

    /*
    Extract Method
    */

    private void validateDuplicateUser(SignUpRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new DuplicateUserException();
        }
    }

    private User getCheckedUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    private void checkPassword(LoginRequest loginRequest, User user) {
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new PasswordErrorException();
        }
    }

    private String validateRefreshToken(TokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();
        String email = tokenRequest.getEmail();
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ExpiredRefreshTokenException();
        }

        Long remainTime = jwtTokenProvider.calValidTime(refreshToken);
        if (remainTime <= 172800000) {
            refreshToken = jwtTokenProvider.createRefreshToken(email);
        }
        return refreshToken;
    }

}
