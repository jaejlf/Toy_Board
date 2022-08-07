package com.filling.good.user.dto.response;

import com.filling.good.user.entity.User;
import com.filling.good.user.enumerate.AuthProvider;
import com.filling.good.user.enumerate.Job;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    Long userId;
    String email;
    String nickname;
    Long fillPercent;
    Job job;
    AuthProvider authProvider;

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .fillPercent(user.getFillPercent())
                .job(user.getJob())
                .authProvider(user.getAuthProvider())
                .build();
    }

}
