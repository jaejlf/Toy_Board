package com.filling.good.domain.issue.dto.request;

import com.filling.good.domain.issue.enumerate.IssueCategory;
import com.filling.good.domain.issue.enumerate.IssueTag;
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
    @NotBlank IssueCategory category;
    @NotBlank IssueTag tag;

}
