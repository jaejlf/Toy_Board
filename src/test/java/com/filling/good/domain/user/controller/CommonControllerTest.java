package com.filling.good.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filling.good.domain.user.entity.User;
import com.filling.good.global.Interceptor.AuthenticationInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static com.filling.good.domain.user.enumerate.AuthProvider.DEFAULT;
import static com.filling.good.domain.user.enumerate.Job.STUDENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriHost = "FILLing-GOOD-URL", uriPort = 8080)
public class CommonControllerTest {

    public MockMvc mockMvc;
    public ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    public AuthenticationInterceptor authenticationInterceptor;

    public User user = new User(
            "filling@naver.com",
            "{{ENCODED_PASSWORD}}",
            "필링굿",
            "필링굿",
            STUDENT,
            DEFAULT
    );

    @BeforeEach
    public void setUp(WebApplicationContext wac, RestDocumentationContextProvider restDoc) {
        
        //인터셉터 통과
        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);
        
        //로그인 정보 세팅
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDoc)
                        .operationPreprocessors()
                        .withRequestDefaults(modifyUris().host("FILLing-GOOD-URL").removePort(), prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
    }

}