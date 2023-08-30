package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.LoginDTO;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.AbstractMySQLContainer;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.utils.TestData;
import com.nagarro.si.pba.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("inttest")
@AutoConfigureMockMvc(addFilters = false)
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LoginControllerIntegrationTest extends AbstractMySQLContainer {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private UserRepository userRepository;

    private final LoginDTO loginDTO = TestData.returnLoginDTOForDonJoe();
    private final LoginDTO loginDTOInvalidPassword = TestData.returnLoginDTOForDonJoeWithInvalidPassword();
    private final User userNotVerified = TestData.returnUserForDonJoeNotVerifiedAndHashed();
    private final User userVerified = TestData.userWithHashedPasswordAndVerified();

    @Test
    public void testLoginSuccessful() throws Exception {
       userRepository.save(userVerified);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    public void testLoginNotVerifiedUser() throws Exception {
        userRepository.save(userNotVerified);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(loginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testLoginInvalidPassword() throws Exception {
        userRepository.save(userVerified);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(loginDTOInvalidPassword)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testLoginNonExistingUser() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(loginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
