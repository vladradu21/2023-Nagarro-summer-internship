package com.nagarro.si.pba.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagarro.si.pba.dto.ExpenseDTO;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.repository.AbstractMySQLContainer;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.UserService;
import com.nagarro.si.pba.utils.TestData;
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
class RecurringExpenseControllerIntegrationTest extends AbstractMySQLContainer {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JWTGenerator jwtGenerator;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    @Test
    void testAddExpense() throws Exception {
        UserDTO savedUser = userService.save(TestData.returnRegisterDTOForDonJoe());
        String token = jwtGenerator.generateJwtForRegistration(savedUser.id(), savedUser.username());
        Token validToken = TestData.returnValidToken();
        validToken.setToken(token);
        tokenService.save(validToken);

        ExpenseDTO expenseDTO = TestData.returnRecurringExpenseDTO();
        MvcResult mvcResult = mockMvc.perform(post("/expenses/recurring")
                .requestAttr("jwtToken", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseDTO)))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseDTO responseExpenseDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExpenseDTO.class);

        assertEquals(expenseDTO.name(), responseExpenseDTO.name());
        assertEquals(expenseDTO.addedDate(), responseExpenseDTO.addedDate());
    }
}
