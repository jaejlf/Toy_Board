package com.filling.good.domain.issue.controller;

import com.filling.good.domain.issue.dto.request.IssueRequest;
import com.filling.good.domain.issue.dto.response.IssueResponse;
import com.filling.good.domain.issue.service.IssueService;
import com.filling.good.domain.user.entity.User;
import com.filling.good.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issue")
public class IssueController {

    private final IssueService issueService;

    @PostMapping("/open")
    public ResponseEntity<Object> openIssue(@RequestBody IssueRequest issueRequest,
                                            @AuthenticationPrincipal User user) {
        IssueResponse issueResponse = issueService.openIssue(issueRequest, user);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("이슈 발행 완료", issueResponse));
    }

    @PatchMapping("/change/{issueId}")
    public ResponseEntity<Object> changeStatus(@PathVariable Long issueId,
                                               @AuthenticationPrincipal User user) {
        IssueResponse issueResponse = issueService.changeStatus(issueId, user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok(issueResponse.getStatus() + " (으)로 상태 변경 완료", issueResponse));
    }

}
