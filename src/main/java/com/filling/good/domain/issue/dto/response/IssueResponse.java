package com.filling.good.domain.issue.dto.response;

import com.filling.good.domain.issue.entity.Issue;
import com.filling.good.domain.issue.enumerate.IssueCategory;
import com.filling.good.domain.issue.enumerate.IssueStatus;
import com.filling.good.domain.issue.enumerate.IssueTag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueResponse {

    Long issueId;
    String title;
    String content;
    String createdDate;
    IssueStatus status;
    IssueCategory category;
    IssueTag tag;
    String writer;

    public static IssueResponse of(Issue issue) {
        return IssueResponse.builder()
                .issueId(issue.getIssueId())
                .title(issue.getTitle())
                .content(issue.getContent())
                .createdDate(issue.getCreatedDate())
                .status(issue.getStatus())
                .category(issue.getCategory())
                .tag(issue.getTag())
                .writer(issue.getWriter().getNickname())
                .build();
    }

}
