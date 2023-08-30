package com.nagarro.si.pba.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagarro.si.pba.dto.GroupDTO;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.service.GroupService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("inttest")
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class GroupControllerIntegrationTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    MockMvc mockMvc;
    private User savedUser;
    private String token;
    List<GroupDTO> savedGroupsDTO;

    @BeforeEach
    public void setup() {
        savedUser = userRepository.save(TestData.returnUserForDonJoe());
        token = jwtGenerator.generateJwtForRegistration(savedUser.getId(), savedUser.getUsername());
        savedGroupsDTO = Arrays.asList(groupService.save(TestData.returnGroupDTO()), groupService.save(TestData.returnGroupDTO()));
    }

    @Test
    void testCreateGroup() throws Exception {
        GroupDTO groupDTO = TestData.returnGroupDTO();

        RequestBuilder request = MockMvcRequestBuilders.post("/groups")
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupDTO));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetGroupById() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/groups/" + savedGroupsDTO.get(0).id())
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(savedGroupsDTO.get(0))));
    }

    @Test
    void testGetAllGroups() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/groups")
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        List<GroupDTO> resultGroupDTOS = objectMapper.readValue(responseContent, new TypeReference<>() {
        });

        assertTrue(resultGroupDTOS.containsAll(savedGroupsDTO));
    }

    @Test
    void testDeleteGroup() throws Exception {
        RequestBuilder deleteRequest = MockMvcRequestBuilders.delete("/groups/" + savedGroupsDTO.get(0).id())
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());

        RequestBuilder getRequest = MockMvcRequestBuilders.get("/groups/" + savedGroupsDTO.get(0).id())
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void testJoinGroup() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.put("/groups/join/" + savedGroupsDTO.get(0).id())
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk());

        int groupId = groupService.getGroupIdByNameAndUserId(savedGroupsDTO.get(0).name(), savedUser.getId());
        assertEquals(savedGroupsDTO.get(0).id(), groupId);
    }

    @Test
    void testLeaveGroup() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/groups/leave/" + savedGroupsDTO.get(0).id())
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk());

        assertEquals(-1, groupService.getGroupIdByNameAndUserId(savedGroupsDTO.get(0).name(), savedUser.getId()));
    }
}