package com.filling.good.domain.issue.service;

import com.filling.good.domain.issue.dto.request.IssueRequest;
import com.filling.good.domain.issue.dto.response.IssueResponse;
import com.filling.good.domain.issue.entity.Issue;
import com.filling.good.domain.issue.repository.IssueRepository;
import com.filling.good.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;

    @Transactional(rollbackOn = {Exception.class})
    public IssueResponse openIssue(IssueRequest issueRequest, User writer) {
        Issue issue = new Issue(
                issueRequest.getTitle(),
                issueRequest.getContent(),
                issueRequest.getCategory(),
                issueRequest.getTag(),
                writer
        );

        return IssueResponse.of(issueRepository.save(issue));
    }

}
