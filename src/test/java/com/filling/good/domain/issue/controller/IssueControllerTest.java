package com.filling.good.domain.issue.controller;

import com.filling.good.domain.issue.dto.request.IssueRequest;
import com.filling.good.domain.issue.dto.response.IssueResponse;
import com.filling.good.domain.issue.enumerate.IssueCategory;
import com.filling.good.domain.issue.enumerate.IssueStatus;
import com.filling.good.domain.issue.enumerate.IssueTag;
import com.filling.good.domain.issue.service.IssueService;
import com.filling.good.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IssueController.class)
@DisplayName("Issue 컨트롤러")
class IssueControllerTest extends CommonControllerTest {

    @MockBean
    private IssueService issueService;

    @DisplayName("이슈 발행 완료")
    @Test
    void openIssue_201() throws Exception {
        //given
        IssueRequest issueRequest = getIssueRequest();
        given(issueService.openIssue(any(), any())).willReturn(getIssueResponse());

        //when
        ResultActions actions = mockMvc.perform(post("/issue/open")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .content(objectMapper.writeValueAsString(issueRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("data").exists())
                .andDo(print())
                .andDo(document("issue_openIssue",
                        requestFields(
                                fieldWithPath("title").description("이슈 제목"),
                                fieldWithPath("content").description("이슈 내용"),
                                fieldWithPath("category").description("이슈 카테고리 (WEB, APP, SERVER, DEVOPS, ETC)"),
                                fieldWithPath("tag").description("이슈 태그 (FEAT, ERROR, LOG, ETC)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data.issueId").description("이슈 고유 아이디"),
                                fieldWithPath("data.title").description("이슈 제목"),
                                fieldWithPath("data.content").description("이슈 내용"),
                                fieldWithPath("data.createdDate").description("이슈 발행 날짜"),
                                fieldWithPath("data.status").description("이슈 상태 (OPEN, CLOSED)"),
                                fieldWithPath("data.category").description("이슈 카테고리"),
                                fieldWithPath("data.tag").description("이슈 태그"),
                                fieldWithPath("data.writer").description("작성자 닉네임"),
                                fieldWithPath("data.myIssue").description("내가 발행한 이슈인지 여부 (true, false)")
                        ))
                );
    }

    /*
    Will Return Object
    */

    private IssueRequest getIssueRequest() {
        return new IssueRequest(
                "에러가 떠요 !",
                "에러는 어쩌구저쩌구",
                "SERVER",
                "ERROR"
        );
    }

    private IssueResponse getIssueResponse() {
        return IssueResponse.builder()
                .issueId(1L)
                .title("에러가 떠요 !")
                .content("에러는 어쩌구저쩌구")
                .createdDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .status(IssueStatus.OPEN)
                .category(IssueCategory.SERVER)
                .tag(IssueTag.ERROR)
                .writer(user.getNickname())
                .myIssue(true)
                .build();
    }

}