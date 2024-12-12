package com.identity.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.identity.lms.common.ErrorResponse;
import com.identity.lms.domain.AuthRequest;
import com.identity.lms.domain.AuthResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Test class for {@link AuthenticationController}
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest implements WithAssertions {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldLoginUserAndReturnJwtToken() throws Exception {
        //given
        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/library/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new AuthRequest("dev", "1234"))))
                .andReturn();
        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(objectMapper.readValue(response.getContentAsString(), AuthResponse.class).getAccessToken())
                .isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenLoginIsNotValid() throws Exception {
        //given
        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/library/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new AuthRequest("unknown", "1234"))))
                .andReturn();
        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(objectMapper.readValue(response.getContentAsString(), ErrorResponse.class).getMessage())
                .isEqualTo("User credentials are not valid");
    }
}