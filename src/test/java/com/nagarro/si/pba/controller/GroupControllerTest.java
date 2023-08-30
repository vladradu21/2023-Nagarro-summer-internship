package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.GroupDTO;
import com.nagarro.si.pba.dto.RoleDTO;
import com.nagarro.si.pba.model.RoleType;
import com.nagarro.si.pba.service.GroupService;
import com.nagarro.si.pba.service.RoleService;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.UserGroupRoleService;
import com.nagarro.si.pba.utils.TestData;
import com.nagarro.si.pba.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {
    @Mock
    private GroupService groupService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserGroupRoleService userGroupRoleService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private GroupController groupController;

    private MockMvc mockMvc;
    private final String mockToken = "mockToken123";
    int userId = 1;
    private final GroupDTO groupDTO = TestData.returnGroupDTO();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
    }

    @Test
    void testCreateGroup() throws Exception {
        when(tokenService.extractUserId(mockToken)).thenReturn(userId);
        RoleDTO roleDTO = TestData.returnRoleDTO();
        when(groupService.save(groupDTO)).thenReturn(groupDTO);
        when(roleService.getByType(RoleType.ADMIN)).thenReturn(roleDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/groups")
                        .requestAttr("jwtToken", mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(groupDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtils.OBJECT_MAPPER.writeValueAsString(groupDTO)));

        verify(userGroupRoleService).assignUserGroupRole(userId, groupDTO.id(), roleDTO.id());
    }

    @Test
    void testGetById() throws Exception {
        when(groupService.getById(groupDTO.id())).thenReturn(groupDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/groups/{id}", groupDTO.id())
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtils.OBJECT_MAPPER.writeValueAsString(groupDTO)));

        verify(groupService).getById(groupDTO.id());
    }

    @Test
    void testGetAll() throws Exception {
        List<GroupDTO> groupDTOList = Arrays.asList(TestData.returnGroupDTO(), TestData.returnGroupDTO());
        when(groupService.getAll()).thenReturn(groupDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/groups")
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtils.OBJECT_MAPPER.writeValueAsString(groupDTOList)));

        verify(groupService).getAll();
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/groups/{id}", groupDTO.id())
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk());

        verify(userGroupRoleService).delete(groupDTO.id());
        verify(groupService).delete(groupDTO.id());
    }

    @Test
    void testJoinGroup() throws Exception {
        RoleDTO roleDTO = TestData.returnRoleDTO();
        int groupId = 1;

        when(tokenService.extractUserId(mockToken)).thenReturn(userId);
        when(roleService.getByType(RoleType.ADMIN)).thenReturn(roleDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/groups/join/{groupId}", groupId)
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk());

        verify(userGroupRoleService).assignUserGroupRole(userId, groupId, roleDTO.id());
    }

    @Test
    void testLeave() throws Exception {
        when(tokenService.extractUserId(mockToken)).thenReturn(userId);
        when(groupService.getGroupIdByNameAndUserId(groupDTO.name(), userId)).thenReturn(groupDTO.id());

        mockMvc.perform(MockMvcRequestBuilders.delete("/groups/leave/{name}", groupDTO.name())
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk());

        verify(userGroupRoleService).removeUserFromGroup(groupDTO.id(), userId);
    }
}