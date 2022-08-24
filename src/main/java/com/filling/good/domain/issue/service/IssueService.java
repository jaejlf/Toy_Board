package com.filling.good.domain.issue.service;

import com.filling.good.domain.issue.dto.request.IssueRequest;
import com.filling.good.domain.issue.dto.response.IssueResponse;
import com.filling.good.domain.issue.entity.Issue;
import com.filling.good.domain.issue.repository.IssueRepository;
import com.filling.good.domain.user.entity.User;
import com.filling.good.global.exception.NoAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.filling.good.domain.issue.enumerate.IssueStatus.CLOSED;
import static com.filling.good.domain.issue.enumerate.IssueStatus.OPEN;

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

    @Transactional(rollbackOn = {Exception.class})
    public IssueResponse changeStatus(Long issueId, User writer) {
        Issue issue = getIssueByIdAndWriter(issueId, writer);
        if(issue.getStatus() == OPEN) issue.changeStatus(CLOSED);
        else issue.changeStatus(OPEN);
        return IssueResponse.of(issueRepository.save(issue));
    }

    /*
    Extract Method
    */

    private Issue getIssueByIdAndWriter(Long issueId, User writer) {
        return issueRepository.findByIssueIdAndWriter(issueId, writer)
                .orElseThrow(NoAuthException::new);
    }

}
