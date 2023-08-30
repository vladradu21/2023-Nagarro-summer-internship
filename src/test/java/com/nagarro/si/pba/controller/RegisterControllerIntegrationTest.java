package com.nagarro.si.pba.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagarro.si.pba.dto.RegisterDTO;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.repository.AbstractMySQLContainer;
import com.nagarro.si.pba.service.UserService;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("inttest")
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RegisterControllerIntegrationTest extends AbstractMySQLContainer {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    void testRegisterNewUser() throws Exception {
        RegisterDTO registerDTO = TestData.returnRegisterDTOForDonJoe();

        MvcResult mvcResult = mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        UserDTO userDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);
        assertEquals(registerDTO.username(), userDTO.username());
        assertEquals(registerDTO.email(), userDTO.email());
        assertEquals(registerDTO.firstName(), userDTO.firstName());
        assertEquals(registerDTO.lastName(), userDTO.lastName());
        assertEquals(registerDTO.country(), userDTO.country());
        assertEquals(registerDTO.age(), userDTO.age());
    }

    @Test
    void testRegisterDuplicateUser() throws Exception {
        // Create a user with the same email or username as an existing user in the database
        RegisterDTO registerDTO = TestData.returnRegisterDTOForDonJoe();

        // Save the user to the database
        userService.save(registerDTO);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(registerDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void testRegisterInvalidRequestData() throws Exception {
        // Create an invalid RegisterDTO with missing required fields
        RegisterDTO registerDTO = null;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(registerDTO)))
                .andExpect(status().isInternalServerError());
    }
}