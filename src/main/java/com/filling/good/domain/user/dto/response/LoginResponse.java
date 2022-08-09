package com.filling.good.domain.user.dto.response;

import com.filling.good.domain.user.enumerate.AuthProvider;
import com.filling.good.domain.user.enumerate.Job;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    Long userId;
    String email;
    String nickname;
    Long fillPercent;
    Job job;
    AuthProvider authProvider;
    String accessToken;

    public static LoginResponse of(UserResponse user, String token) {
        return LoginResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .fillPercent(user.getFillPercent())
                .job(user.getJob())
                .authProvider(user.getAuthProvider())
                .accessToken(token)
                .build();
    }

}
