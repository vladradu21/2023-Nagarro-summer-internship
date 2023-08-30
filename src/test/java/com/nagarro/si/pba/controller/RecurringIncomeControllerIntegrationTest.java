package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.IncomeDTO;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.repository.AbstractMySQLContainer;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.UserService;
import com.nagarro.si.pba.utils.TestData;
import com.nagarro.si.pba.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("inttest")
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RecurringIncomeControllerIntegrationTest extends AbstractMySQLContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private UserService userService;
    private String token;

    @BeforeEach
    public void setUp() {
        UserDTO savedUser = userService.save(TestData.returnRegisterDTOForDonJoe());
        token = jwtGenerator.generateJwtForRegistration(savedUser.id(), savedUser.username());

        Token validToken = TestData.returnValidToken();
        validToken.setToken(token);
        tokenService.save(validToken);
    }

    @Test
    void addRecurringIncome() throws Exception {
        IncomeDTO incomeDTO = TestData.returnRecurringIncomeJustForUserDTO();

        MvcResult mvcResult = mockMvc.perform(post("/incomes/recurring")
                        .requestAttr("jwtToken", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(incomeDTO)))
                .andExpect(status().isOk())
                .andReturn();

        IncomeDTO responseIncomeDTO = TestUtils.OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), IncomeDTO.class);

        assertEquals(incomeDTO.amount(), responseIncomeDTO.amount());
        assertEquals(incomeDTO.currency(), responseIncomeDTO.currency());
        assertEquals(incomeDTO.repetitionFlow(), responseIncomeDTO.repetitionFlow());
    }
}