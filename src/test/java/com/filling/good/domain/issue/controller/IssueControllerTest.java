package com.filling.good.domain.issue.controller;

import com.filling.good.domain.issue.dto.request.IssueRequest;
import com.filling.good.domain.issue.dto.response.IssueResponse;
import com.filling.good.domain.issue.service.IssueService;
import com.filling.good.global.exception.NoAuthException;
import com.filling.good.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.filling.good.domain.issue.enumerate.IssueCategory.SERVER;
import static com.filling.good.domain.issue.enumerate.IssueStatus.OPEN;
import static com.filling.good.domain.issue.enumerate.IssueTag.ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
    void openIssue_201(TestInfo testInfo) throws Exception {
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
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/issue/" + testInfo.getDisplayName(),
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

    @DisplayName("이슈 상태 변경 완료")
    @Test
    void changeStatus_200(TestInfo testInfo) throws Exception {
        //given
        long issueId = 1L;
        given(issueService.changeStatus(any(), any())).willReturn(getIssueResponse());

        //when
        ResultActions actions = mockMvc.perform(patch("/issue/change/{issueId}", issueId)
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/issue/" + testInfo.getDisplayName(),
                        pathParameters(
                                parameterWithName("issueId").description("상태를 변경할 이슈의 아이디")
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

    @DisplayName("이슈 상태 변경 실패 (존재하지 않는 이슈)")
    @Test
    void changeStatus_EntityNotFoundException(TestInfo testInfo) throws Exception {
        //given
        long issueId = 1L;
        given(issueService.changeStatus(any(), any())).willThrow(new EntityNotFoundException());

        //when
        ResultActions actions = mockMvc.perform(patch("/issue/change/{issueId}", issueId)
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errName").value("EntityNotFoundException"))
                .andDo(document("/issue/" + testInfo.getDisplayName(),
                        pathParameters(
                                parameterWithName("issueId").description("상태를 변경할 이슈의 아이디")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("errName").description("예외 이름"),
                                fieldWithPath("message").description("예외 메세지")
                        ))
                );
    }

    @DisplayName("이슈 상태 변경 실패 (권한이 없는 유저)")
    @Test
    void changeStatus_NoAuthException(TestInfo testInfo) throws Exception {
        //given
        long issueId = 1L;
        given(issueService.changeStatus(any(), any())).willThrow(new NoAuthException());

        //when
        ResultActions actions = mockMvc.perform(patch("/issue/change/{issueId}", issueId)
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errName").value("NoAuthException"))
                .andDo(document("/issue/" + testInfo.getDisplayName(),
                        pathParameters(
                                parameterWithName("issueId").description("상태를 변경할 이슈의 아이디")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("errName").description("예외 이름"),
                                fieldWithPath("message").description("예외 메세지")
                        ))
                );
    }

    @DisplayName("특정 이슈 조회 완료")
    @Test
    void getIssueOne_200(TestInfo testInfo) throws Exception {
        //given
        long issueId = 1L;
        given(issueService.getIssueOne(any(), any())).willReturn(getIssueResponse());

        //when
        ResultActions actions = mockMvc.perform(get("/issue/{issueId}", issueId)
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/issue/" + testInfo.getDisplayName(),
                        pathParameters(
                                parameterWithName("issueId").description("조회할 이슈의 아이디")
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

    @DisplayName("특정 이슈 조회 실패 (존재하지 않는 이슈)")
    @Test
    void getIssueOne_EntityNotFoundException(TestInfo testInfo) throws Exception {
        //given
        long issueId = 1L;
        given(issueService.getIssueOne(any(), any())).willThrow(new EntityNotFoundException());

        //when
        ResultActions actions = mockMvc.perform(get("/issue/{issueId}", issueId)
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errName").value("EntityNotFoundException"))
                .andDo(document("/issue/" + testInfo.getDisplayName(),
                        pathParameters(
                                parameterWithName("issueId").description("조회할 이슈의 아이디")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("errName").description("예외 이름"),
                                fieldWithPath("message").description("예외 메세지")
                        ))
                );
    }

    @DisplayName("특정 이슈 조회 실패 (권한이 없는 유저)")
    @Test
    void getIssueOne_NoAuthException(TestInfo testInfo) throws Exception {
        //given
        long issueId = 1L;
        given(issueService.getIssueOne(any(), any())).willThrow(new NoAuthException());

        //when
        ResultActions actions = mockMvc.perform(get("/issue/{issueId}", issueId)
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errName").value("NoAuthException"))
                .andDo(document("/issue/" + testInfo.getDisplayName(),
                        pathParameters(
                                parameterWithName("issueId").description("조회할 이슈의 아이디")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("errName").description("예외 이름"),
                                fieldWithPath("message").description("예외 메세지")
                        ))
                );
    }

    @DisplayName("특정 이슈 삭제 완료")
    @Test
    void deleteIssue_200(TestInfo testInfo) throws Exception {
        //given
        long issueId = 1L;

        //when
        ResultActions actions = mockMvc.perform(delete("/issue/{issueId}", issueId)
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("/issue/" + testInfo.getDisplayName(),
                        pathParameters(
                                parameterWithName("issueId").description("삭제할 이슈의 아이디")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data").description("결과 데이터")
                        ))
                );
    }

    @DisplayName("특정 이슈 삭제 실패 (존재하지 않는 이슈)")
    @Test
    void deleteIssue_EntityNotFoundException(TestInfo testInfo) throws Exception {
        //given
        long issueId = 1L;
        doThrow(new EntityNotFoundException()).when(issueService).deleteIssue(any(), any());

        //when
        ResultActions actions = mockMvc.perform(delete("/issue/{issueId}", issueId)
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errName").value("EntityNotFoundException"))
                .andDo(document("/issue/" + testInfo.getDisplayName(),
                        pathParameters(
                                parameterWithName("issueId").description("삭제할 이슈의 아이디")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("errName").description("예외 이름"),
                                fieldWithPath("message").description("예외 메세지")
                        ))
                );
    }

    @DisplayName("특정 이슈 삭제 실패 (권한이 없는 유저)")
    @Test
    void deleteIssue_NoAuthException(TestInfo testInfo) throws Exception {
        //given
        long issueId = 1L;
        doThrow(new NoAuthException()).when(issueService).deleteIssue(any(), any());

        //when
        ResultActions actions = mockMvc.perform(delete("/issue/{issueId}", issueId)
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errName").value("NoAuthException"))
                .andDo(document("/issue/" + testInfo.getDisplayName(),
                        pathParameters(
                                parameterWithName("issueId").description("조회할 이슈의 아이디")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("errName").description("예외 이름"),
                                fieldWithPath("message").description("예외 메세지")
                        ))
                );
    }

    @DisplayName("특정 유저가 작성한 이슈 목록 조회 완료")
    @Test
    void getIssueByUserId_200(TestInfo testInfo) throws Exception {
        //given
        long userId = 1L;
        given(issueService.getIssueByUserId(any(), any())).willReturn(getIssueResponseList());

        //when
        ResultActions actions = mockMvc.perform(get("/issue/list/writer/{userId}", userId)
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/issue/" + testInfo.getDisplayName(),
                        pathParameters(
                                parameterWithName("userId").description("조회할 유저의 아이디")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                subsectionWithPath("data").type(JsonFieldType.ARRAY).description("이슈 목록")
                        ))
                );
    }

    @DisplayName("특정 카테고리의 이슈 목록 조회 완료")
    @Test
    void getIssueByCategory_200(TestInfo testInfo) throws Exception {
        //given
        String category = "SERVER";
        given(issueService.getIssueByUserId(any(), any())).willReturn(getIssueResponseList());

        //when
        ResultActions actions = mockMvc.perform(get("/issue/list/category/{category}", category)
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/issue/" + testInfo.getDisplayName(),
                        pathParameters(
                                parameterWithName("category").description("조회할 카테고리 (WEB, APP, SERVER, DEVOPS, ETC)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                subsectionWithPath("data").type(JsonFieldType.ARRAY).description("이슈 목록")
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
                .status(OPEN)
                .category(SERVER)
                .tag(ERROR)
                .writer(user.getNickname())
                .myIssue(true)
                .build();
    }

    private List<IssueResponse> getIssueResponseList() {
        List<IssueResponse> issueResponseList = new ArrayList<>();
        issueResponseList.add(getIssueResponse());
        issueResponseList.add(getIssueResponse());
        return issueResponseList;
    }


}