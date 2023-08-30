package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.CategorySettingsDTO;
import com.nagarro.si.pba.service.GroupCategoryService;
import com.nagarro.si.pba.service.GroupService;
import com.nagarro.si.pba.service.TokenService;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GroupCategoryControllerTest {

    private final String mockToken = "mockToken123";
    private final String groupName = "testGroup";
    private final int mockUserId = 1;
    private final int mockGroupId = 10;
    private MockMvc mockMvc;
    @Mock
    private GroupCategoryService groupCategoryService;

    @Mock
    private GroupService groupService;

    @Mock
    private TokenService tokenService;
    @InjectMocks
    private GroupCategoryController groupCategoryController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupCategoryController).build();
    }

    @Test
    void testGetCategories() throws Exception {
        when(tokenService.extractUserId(mockToken)).thenReturn(mockUserId);
        when(groupService.getGroupIdByNameAndUserId(groupName, mockUserId)).thenReturn(mockGroupId);

        CategorySettingsDTO categorySettingsDTO = TestData.returnCategorySettingsDTO();
        when(groupCategoryService.getCategories(mockGroupId)).thenReturn(categorySettingsDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/group-categories/" + groupName)
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtils.OBJECT_MAPPER.writeValueAsString(categorySettingsDTO)));

        verify(tokenService).extractUserId(mockToken);
        verify(groupService).getGroupIdByNameAndUserId(groupName, mockUserId);
        verify(groupCategoryService).getCategories(mockGroupId);
    }

    @Test
    void testUpdateCategories() throws Exception {
        when(tokenService.extractUserId(mockToken)).thenReturn(mockUserId);
        when(groupService.getGroupIdByNameAndUserId(groupName, mockUserId)).thenReturn(mockGroupId);

        CategorySettingsDTO categorySettingsDTO = TestData.returnCategorySettingsDTO();
        when(groupCategoryService.update(mockGroupId, categorySettingsDTO)).thenReturn(categorySettingsDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/group-categories/" + groupName)
                        .requestAttr("jwtToken", mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(categorySettingsDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtils.OBJECT_MAPPER.writeValueAsString(categorySettingsDTO)));

        verify(tokenService).extractUserId(mockToken);
        verify(groupService).getGroupIdByNameAndUserId(groupName, mockUserId);
        verify(groupCategoryService).update(mockGroupId, categorySettingsDTO);
    }

    @Test
    void testDeleteCategory() throws Exception {
        when(tokenService.extractUserId(mockToken)).thenReturn(mockUserId);
        when(groupService.getGroupIdByNameAndUserId(groupName, mockUserId)).thenReturn(mockGroupId);

        int categoryId = 5;

        mockMvc.perform(MockMvcRequestBuilders.delete("/group-categories/" + groupName + "/" + categoryId)
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk());

        verify(tokenService).extractUserId(mockToken);
        verify(groupService).getGroupIdByNameAndUserId(groupName, mockUserId);
        verify(groupCategoryService).delete(mockGroupId, categoryId);
    }
}
