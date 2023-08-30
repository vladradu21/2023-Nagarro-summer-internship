package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.repository.AbstractMySQLContainer;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.UserService;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("inttest")
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ResetPasswordControllerIntegrationTest extends AbstractMySQLContainer {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JWTGenerator jwtGenerator;
    private String token;
    private UserDTO savedUser;

    @BeforeEach
    public void setUp() {
        savedUser = userService.save(TestData.returnRegisterDTOForDonJoe());
        token = jwtGenerator.generateJwtForRegistration(savedUser.id(), savedUser.username());

        Token validToken = new Token();
        validToken.setId(1);
        validToken.setToken(token);
        tokenService.save(validToken);
    }

    @Test
    void resetPassword_whenTokenIsValid() throws Exception {
        String newPasswordJson = "{\"password\":\"NewPassword\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/update-password")
                        .requestAttr("jwtToken", token)
                        .content(newPasswordJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Password has been reset successfully."));
    }

    @Test
    void triggerResetPassword_whenEmailIsValid() throws Exception {
        String emailJson = "{\"email\":\"" + savedUser.email() + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/reset-password")
                        .content(emailJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void resetPassword_whenPasswordIsTooLong_shouldReturnBadRequest() throws Exception {
        String newPasswordJson = "{\"password\":\"" + "ThisPasswordIsToLooooooooooooong" + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/update-password")
                        .requestAttr("jwtToken", token)
                        .content(newPasswordJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}