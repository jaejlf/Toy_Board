package com.filling.good.domain.user.dto.response;

import com.filling.good.domain.user.entity.User;
import com.filling.good.domain.user.enumerate.AuthProvider;
import com.filling.good.domain.user.enumerate.Job;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    Long userId;
    String email;
    String nickname;
    String name;
    Long fillPercent;
    Job job;
    AuthProvider authProvider;

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .name(user.getName())
                .fillPercent(user.getFillPercent())
                .job(user.getJob())
                .authProvider(user.getAuthProvider())
                .build();
    }

}
