package com.nagarro.si.pba.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public abstract class BaseControllerTest<T> {

    protected MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(getController())
                .build();
    }

    protected abstract T getController();
}