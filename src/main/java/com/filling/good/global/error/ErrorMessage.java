package com.filling.good.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    USER_ALREADY_EXIST("이미 가입된 유저입니다."),
    USER_NOT_FOUND("가입되지 않은 유저입니다."),
    PASSWORD_ERROR("잘못된 비밀번호 입니다.");

    private final String msg;

}
