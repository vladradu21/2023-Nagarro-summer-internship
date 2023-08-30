package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.CategorySettingsDTO;
import com.nagarro.si.pba.service.UserCategoryService;
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
class UserCategoryControllerTest {

    private final String mockToken = "mockToken123";
    private final int mockUserId = 1;
    private MockMvc mockMvc;

    @Mock
    private UserCategoryService userCategoryService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserCategoryController userCategoryController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userCategoryController).build();
    }

    @Test
    void testGetCategories() throws Exception {
        when(tokenService.extractUserId(mockToken)).thenReturn(mockUserId);

        CategorySettingsDTO categorySettingsDTO = TestData.returnCategorySettingsDTO();
        when(userCategoryService.getCategories(mockUserId)).thenReturn(categorySettingsDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/user-categories")
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtils.OBJECT_MAPPER.writeValueAsString(categorySettingsDTO)));

        verify(tokenService).extractUserId(mockToken);
        verify(userCategoryService).getCategories(mockUserId);
    }

    @Test
    void testUpdateCategories() throws Exception {
        when(tokenService.extractUserId(mockToken)).thenReturn(mockUserId);

        CategorySettingsDTO categorySettingsDTO = TestData.returnCategorySettingsDTO();
        when(userCategoryService.update(mockUserId, categorySettingsDTO)).thenReturn(categorySettingsDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/user-categories")
                        .requestAttr("jwtToken", mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(categorySettingsDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtils.OBJECT_MAPPER.writeValueAsString(categorySettingsDTO)));

        verify(tokenService).extractUserId(mockToken);
        verify(userCategoryService).update(mockUserId, categorySettingsDTO);
    }

    @Test
    void testDeleteCategory() throws Exception {
        when(tokenService.extractUserId(mockToken)).thenReturn(mockUserId);

        int categoryId = 5;

        mockMvc.perform(MockMvcRequestBuilders.delete("/user-categories/" + categoryId)
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk());

        verify(tokenService).extractUserId(mockToken);
        verify(userCategoryService).delete(mockUserId, categoryId);
    }
}
