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

import java.util.List;

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
                .body(ResultResponse.create("이슈 발행", issueResponse));
    }

    @PatchMapping("/change/{issueId}")
    public ResponseEntity<Object> changeStatus(@PathVariable Long issueId,
                                               @AuthenticationPrincipal User user) {
        IssueResponse issueResponse = issueService.changeStatus(issueId, user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok(issueResponse.getStatus() + "(으)로 상태 변경", issueResponse));
    }

    @GetMapping("/{issueId}")
    public ResponseEntity<Object> getIssueOne(@PathVariable Long issueId,
                                              @AuthenticationPrincipal User user) {
        IssueResponse issueResponse = issueService.getIssueOne(issueId, user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok(issueId + "번 이슈 조회", issueResponse));
    }

    @DeleteMapping("/{issueId}")
    public ResponseEntity<Object> deleteIssue(@PathVariable Long issueId,
                                              @AuthenticationPrincipal User user) {
        issueService.deleteIssue(issueId, user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok(issueId + "번 이슈 삭제"));
    }

    @GetMapping("/list/writer/{userId}")
    public ResponseEntity<Object> getIssueByUserId(@PathVariable Long userId,
                                                   @AuthenticationPrincipal User user) {
        List<IssueResponse> issueResponseList = issueService.getIssueByUserId(userId, user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok(userId + "번 유저의 이슈 조회", issueResponseList));
    }

    @GetMapping("/list/category/{category}")
    public ResponseEntity<Object> getIssueByCategory(@PathVariable String category,
                                                     @AuthenticationPrincipal User user) {
        List<IssueResponse> issueResponseList = issueService.getIssueByCategory(category, user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok(category + " 카테고리 이슈 조회", issueResponseList));
    }

}
