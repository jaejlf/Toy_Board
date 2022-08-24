package com.filling.good.domain.issue.service;

import com.filling.good.domain.issue.dto.request.IssueRequest;
import com.filling.good.domain.issue.dto.response.IssueResponse;
import com.filling.good.domain.issue.entity.Issue;
import com.filling.good.domain.issue.repository.IssueRepository;
import com.filling.good.domain.user.entity.User;
import com.filling.good.global.exception.NoAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import static com.filling.good.domain.issue.enumerate.IssueStatus.CLOSED;
import static com.filling.good.domain.issue.enumerate.IssueStatus.OPEN;
import static com.filling.good.global.exception.ErrorMessage.ISSUE_NOT_FOUND;

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

        return IssueResponse.of(issueRepository.save(issue), writer);
    }

    @Transactional(rollbackOn = {Exception.class})
    public IssueResponse changeStatus(Long issueId, User writer) {
        Issue issue = getIssueByIdAndWriter(issueId, writer);
        if (issue.getStatus() == OPEN) issue.changeStatus(CLOSED);
        else issue.changeStatus(OPEN);
        return IssueResponse.of(issueRepository.save(issue), writer);
    }

    @Transactional(rollbackOn = {Exception.class})
    public IssueResponse getIssueOne(Long issueId, User user) {
        Issue issue = getIssue(issueId);
        return IssueResponse.of(issue, user);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void deleteIssue(Long issueId, User user) {
        Issue issue = getIssueByIdAndWriter(issueId, user);

        /*
        (추후 구현) 해당 이슈에 활동 내역이 있을 경우 삭제 불가
        */

        issueRepository.delete(issue);
    }

    /*
    Extract Method
    */

    private Issue getIssue(Long issueId) {
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException(ISSUE_NOT_FOUND.getMsg()));
    }

    private Issue getIssueByIdAndWriter(Long issueId, User writer) {
        return issueRepository.findByIssueIdAndWriter(issueId, writer)
                .orElseThrow(NoAuthException::new);
    }

}
