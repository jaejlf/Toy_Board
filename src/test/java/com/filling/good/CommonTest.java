package com.filling.good;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filling.good.global.Interceptor.AuthenticationInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriHost = "FILLing-GOOD-URL", uriPort = 8080)
public class CommonTest {

    public MockMvc mockMvc;
    public ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    public AuthenticationInterceptor authenticationInterceptor;

    @BeforeEach
    public void setUp(WebApplicationContext wac, RestDocumentationContextProvider restDoc) {
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