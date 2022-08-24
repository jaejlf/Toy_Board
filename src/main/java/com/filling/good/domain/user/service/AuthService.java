package com.filling.good.domain.user.service;

import com.filling.good.domain.user.dto.request.LoginRequest;
import com.filling.good.domain.user.dto.request.ReissueRequest;
import com.filling.good.domain.user.dto.request.SignUpRequest;
import com.filling.good.domain.user.dto.response.TokenResponse;
import com.filling.good.domain.user.dto.response.UserResponse;
import com.filling.good.domain.user.entity.User;
import com.filling.good.domain.user.enumerate.Job;
import com.filling.good.domain.user.exception.CustomJwtException;
import com.filling.good.domain.user.exception.InvalidTokenException;
import com.filling.good.domain.user.exception.LoginRequestException;
import com.filling.good.domain.user.repository.UserRepository;
import com.filling.good.global.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;
import java.util.Objects;

import static com.filling.good.domain.user.enumerate.AuthProvider.DEFAULT;
import static com.filling.good.global.exception.ErrorMessage.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Transactional(rollbackOn = {Exception.class})
    public UserResponse join(SignUpRequest signUpRequest) {
        validateDuplicateUser(signUpRequest);
        User user = new User(
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getNickname(),
                signUpRequest.getName(),
                Job.findJobCode(signUpRequest.getJobValue()),
                DEFAULT
        );

        return UserResponse.of(userRepository.save(user));
    }

    @Transactional(rollbackOn = {Exception.class})
    public TokenResponse defaultLogin(LoginRequest loginRequest) {
        User user = getUserByEmail(loginRequest.getEmail());
        if (user.getAuthProvider() != DEFAULT) throw new LoginRequestException();

        checkPassword(loginRequest.getPassword(), user.getPassword());
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getAuthProvider());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getAuthProvider());
        return TokenResponse.of(accessToken, refreshToken);
    }

    @Transactional(rollbackOn = {Exception.class})
    public TokenResponse tokenReIssue(ReissueRequest reissueRequest) {
        User user = getUserByEmail(reissueRequest.getEmail());
        String refreshToken = getCheckedRefreshToken(reissueRequest, user);
        String accessToken = jwtTokenProvider.createAccessToken(reissueRequest.getEmail(), user.getAuthProvider());
        return TokenResponse.of(accessToken, refreshToken);
    }

    /*
    Extract Method
    */

    private void validateDuplicateUser(SignUpRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new EntityExistsException(USER_ALREADY_EXIST.getMsg());
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND.getMsg()));
    }

    private void checkPassword(String inputPw, String userPw) {
        if (!passwordEncoder.matches(inputPw, userPw)) {
            throw new IllegalArgumentException(PASSWORD_ERROR.getMsg());
        }
    }

    private String getCheckedRefreshToken(ReissueRequest tokenRequest, User user) {

        //리프레쉬 토큰 유효성 체크
        String refreshToken = tokenRequest.getRefreshToken();
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomJwtException(FORBIDDEN, "리프레쉬 토큰");
        }

        //DB에 저장된 refresh 토큰과 일치하는지 체크
        String email = tokenRequest.getEmail();
        String storedRefreshToken = redisService.getValues(user.getEmail());
        if (!Objects.equals(storedRefreshToken, refreshToken)) {
            throw new InvalidTokenException();
        }

        //토큰 만료 기간이 2일 이내로 남았을 경우 재발급
        Long remainTime = jwtTokenProvider.calValidTime(refreshToken);
        if (remainTime <= 172800000) {
            refreshToken = jwtTokenProvider.createRefreshToken(email, user.getAuthProvider());
        }
        return refreshToken;

    }

}
