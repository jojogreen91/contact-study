package com.fastcampus.javaallinone.project3.mycontact.controller;

import com.fastcampus.javaallinone.project3.mycontact.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class HelloWorldControllerTest {

    @Autowired
    private HelloWorldController helloWorldController;
    // GlobalExceptionHandler 를 적용하기 위한 설정, 테스트를 할 때
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;
    @Autowired
    private WebApplicationContext wac;

    // MockMvc 선언
    private MockMvc mockMvc;
    // 다른 메서드가 실행되기 전에 미리 실행되어야 되는 부분을 설정하는 메서드
    @BeforeEach
    void beforeEach () {
        mockMvc = MockMvcBuilders
                .standaloneSetup(helloWorldController)
                // GlobalExceptionHandler 를 적용하기 위한 설정, 테스트를 할 때
                .setControllerAdvice(globalExceptionHandler)
                .alwaysDo(print())
                .build();
    }

    @Test
    void helloWorld () {
//        System.out.println("test");
        System.out.println(helloWorldController.helloWorld());

        assertThat(helloWorldController.helloWorld()).isEqualTo("HelloWorld");
    }

    // MockMvc 사용
    @Test
    void mockMvcTest () throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/helloWorld"))
                .andExpect(status().isOk())
                .andExpect(content().string("HelloWorld"));
    }

    @Test
    void helloException () throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/helloException"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Server Error!!!"));
    }
}