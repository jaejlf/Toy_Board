package com.filling.good.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank String email;
    @NotBlank String password;
    @NotBlank String nickname;
    @NotBlank String name;
    @NotBlank String jobValue;

}