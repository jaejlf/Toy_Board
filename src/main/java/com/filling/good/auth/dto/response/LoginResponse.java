package com.filling.good.auth.dto.response;

import com.filling.good.user.dto.response.UserResponse;
import com.filling.good.user.enumerate.AuthProvider;
import com.filling.good.user.enumerate.Job;
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
