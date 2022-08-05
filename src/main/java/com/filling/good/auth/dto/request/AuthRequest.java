package com.filling.good.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class AuthRequest {

    @NotBlank String email;
    @NotBlank String password;
    @NotBlank String nickname;
    @NotBlank String jobValue;

}
