package com.filling.good.auth.dto.response;

import com.filling.good.user.entity.User;
import com.filling.good.user.enumerate.Job;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    Long userId;
    String email;
    String nickname;
    Long fillPercent;
    Job job;

    public static AuthResponse res(User user) {
        return AuthResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .fillPercent(user.getFillPercent())
                .job(user.getJob())
                .build();
    }

}
