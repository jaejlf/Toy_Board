package com.filling.good.domain.issue.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IssueRequest {

    @NotBlank String title;
    @NotBlank String content;
    @NotBlank String category;
    @NotBlank String tag;

}
