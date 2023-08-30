package com.nagarro.si.pba.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.AbstractMySQLContainer;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("inttest")
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserControllerIntegrationTest extends AbstractMySQLContainer {
    private final List<User> users = Arrays.asList(TestData.returnUserForDonJoe(), TestData.returnUserForBobDumbledore(),
            TestData.returnUserForMichaelStevens());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        for (User user : users) {
            userRepository.save(user);
        }
    }

    @Test
    void testGetAllUsers() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/users");
        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();

        List<UserDTO> userDTOs = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
        List<String> emails = userDTOs.stream().map(UserDTO::email).collect(Collectors.toList());
        List<String> expectedEmails = users.stream().map(User::getEmail).collect(Collectors.toList());

        Assertions.assertEquals(expectedEmails, emails);
    }

    @Test
    void testGetUserById() throws Exception {
        User user = users.get(0);
        RequestBuilder request = MockMvcRequestBuilders.get("/users/" + user.getId());

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        UserDTO userDTO = objectMapper.readValue(responseContent, UserDTO.class);

        Assertions.assertEquals(user.getEmail(), userDTO.email());
    }

    @Test
    void testGetUserByEmail() throws Exception {
        User user = users.get(0);
        RequestBuilder request = MockMvcRequestBuilders.get("/users/email/" + user.getEmail());
        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        UserDTO userDTO = objectMapper.readValue(responseContent, UserDTO.class);

        Assertions.assertEquals(user.getEmail(), userDTO.email());
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        long invalidUserId = 9999;
        RequestBuilder request = MockMvcRequestBuilders.get("/users/" + invalidUserId);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserByEmail_NotFound() throws Exception {
        String invalidEmail = "invalidEmail";
        RequestBuilder request = MockMvcRequestBuilders.get("/users/email/" + invalidEmail);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
}
