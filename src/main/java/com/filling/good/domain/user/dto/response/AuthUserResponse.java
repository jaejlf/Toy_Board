package com.filling.good.domain.user.dto.response;

import com.filling.good.domain.user.entity.User;
import com.filling.good.domain.user.enumerate.AuthProvider;
import com.filling.good.domain.user.enumerate.Job;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthUserResponse {

    Long userId;
    String email;
    String nickname;
    Long fillPercent;
    Job job;
    AuthProvider authProvider;
    String accessToken;
    String refreshToken;

    public static AuthUserResponse of(User user, String accessToken, String refreshToken) {
        return AuthUserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .fillPercent(user.getFillPercent())
                .job(user.getJob())
                .authProvider(user.getAuthProvider())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
