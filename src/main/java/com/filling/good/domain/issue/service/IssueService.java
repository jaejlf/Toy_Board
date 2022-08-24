package com.filling.good.domain.issue.service;

import com.filling.good.domain.issue.dto.request.IssueRequest;
import com.filling.good.domain.issue.dto.response.IssueResponse;
import com.filling.good.domain.issue.entity.Issue;
import com.filling.good.domain.issue.enumerate.IssueCategory;
import com.filling.good.domain.issue.enumerate.IssueTag;
import com.filling.good.domain.issue.repository.IssueRepository;
import com.filling.good.domain.user.entity.User;
import com.filling.good.domain.user.service.AuthService;
import com.filling.good.global.exception.NoAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.filling.good.domain.issue.enumerate.IssueStatus.CLOSED;
import static com.filling.good.domain.issue.enumerate.IssueStatus.OPEN;
import static com.filling.good.global.exception.ErrorMessage.ISSUE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final AuthService authService;

    @Transactional(rollbackOn = {Exception.class})
    public IssueResponse openIssue(IssueRequest issueRequest, User writer) {
        Issue issue = new Issue(
                issueRequest.getTitle(),
                issueRequest.getContent(),
                IssueCategory.valueOf(issueRequest.getCategory()),
                IssueTag.valueOf(issueRequest.getTag()),
                writer
        );

        return IssueResponse.of(issueRepository.save(issue), writer);
    }

    @Transactional(rollbackOn = {Exception.class})
    public IssueResponse changeStatus(Long issueId, User user) {
        Issue issue = getIssue(issueId);
        checkAuth(issue, user);
        if (issue.getStatus() == OPEN) issue.changeStatus(CLOSED);
        else issue.changeStatus(OPEN);
        return IssueResponse.of(issueRepository.save(issue), user);
    }

    @Transactional(rollbackOn = {Exception.class})
    public IssueResponse getIssueOne(Long issueId, User user) {
        Issue issue = getIssue(issueId);
        return IssueResponse.of(issue, user);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void deleteIssue(Long issueId, User user) {
        Issue issue = getIssue(issueId);
        checkAuth(issue, user);

        /*
        (추후 구현) 해당 이슈에 활동 내역이 있을 경우 삭제 불가
        */

        issueRepository.delete(issue);
    }

    @Transactional(rollbackOn = {Exception.class})
    public List<IssueResponse> getIssueByUserId(Long userId, User user) {
        User target = authService.getUserById(userId);
        List<Issue> issueList = issueRepository.findByWriter(target);
        return issueListConvertToDtoList(issueList, user);
    }

    @Transactional(rollbackOn = {Exception.class})
    public List<IssueResponse> getIssueByCategory(String category, User user) {
        List<Issue> issueList = issueRepository.findByCategory(IssueCategory.valueOf(category));
        return issueListConvertToDtoList(issueList, user);
    }

    /*
    Extract Method
    */

    private Issue getIssue(Long issueId) {
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException(ISSUE_NOT_FOUND.getMsg()));
    }

    private void checkAuth(Issue issue, User user) {
        if (issue.getWriter() != user) throw new NoAuthException();
    }

    private List<IssueResponse> issueListConvertToDtoList(List<Issue> issueList, User user) {
        List<IssueResponse> issueResponseList = new ArrayList<>();
        for (Issue issue : issueList) {
            issueResponseList.add(IssueResponse.of(issue, user));
        }
        Collections.reverse(issueResponseList); //최신순
        return issueResponseList;
    }

}
