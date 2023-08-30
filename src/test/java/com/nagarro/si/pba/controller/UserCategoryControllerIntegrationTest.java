package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.CategoryDTO;
import com.nagarro.si.pba.dto.CategorySettingsDTO;
import com.nagarro.si.pba.model.Category;
import com.nagarro.si.pba.model.CategoryType;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.AbstractMySQLContainer;
import com.nagarro.si.pba.repository.UserCategoryRepository;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.security.JWTGenerator;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("inttest")
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserCategoryControllerIntegrationTest extends AbstractMySQLContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCategoryRepository userCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTGenerator jwtGenerator;

    private User savedUser;
    private String token;

    @BeforeEach
    public void setup() {
        savedUser = userRepository.save(TestData.returnUserForDonJoe());
        token = jwtGenerator.generateJwtForRegistration(savedUser.getId(), savedUser.getUsername());
    }

    @Test
    void testGetCategories() throws Exception {
        CategorySettingsDTO expectedCategorySettingsDTO = TestData.returnSampleCategorySettingsDTO();

        RequestBuilder request = MockMvcRequestBuilders.get("/user-categories")
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token);

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();
        CategorySettingsDTO actualCategorySettingsDTO = TestUtils.OBJECT_MAPPER.readValue(responseContent, CategorySettingsDTO.class);

        assertEquals(expectedCategorySettingsDTO, actualCategorySettingsDTO);
    }

    @Test
    void testUpdateCategories() throws Exception {
        List<CategoryDTO> incomes = List.of(TestData.getNewCategoryDTO());
        List<CategoryDTO> expenses = new ArrayList<>();

        CategorySettingsDTO expectedCategorySettingsDTO = new CategorySettingsDTO(incomes, expenses);

        RequestBuilder request = MockMvcRequestBuilders.put("/user-categories")
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.OBJECT_MAPPER.writeValueAsString(expectedCategorySettingsDTO));

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        CategorySettingsDTO responseCategorySettingsDTO = TestUtils.OBJECT_MAPPER.readValue(responseContent, CategorySettingsDTO.class);

        assertEquals(expectedCategorySettingsDTO, responseCategorySettingsDTO);

    }

    @Test
    void testAddNewCategory() throws Exception {
        CategoryDTO newSalaryCategory = new CategoryDTO(null, "bonusSalary", CategoryType.INCOME, true, true);

        List<CategoryDTO> incomes = List.of(newSalaryCategory);
        List<CategoryDTO> expenses = new ArrayList<>();

        CategorySettingsDTO newCategorySettingsDTO = new CategorySettingsDTO(incomes, expenses);

        RequestBuilder request = MockMvcRequestBuilders.put("/user-categories")
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.OBJECT_MAPPER.writeValueAsString(newCategorySettingsDTO));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // Get all categories to ensure the new one is added
        RequestBuilder getRequest = MockMvcRequestBuilders.get("/user-categories")
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token);

        MvcResult mvcResult = mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        CategorySettingsDTO returnedCategories = TestUtils.OBJECT_MAPPER.readValue(responseContent, CategorySettingsDTO.class);

        // Assert the new category is present in the returned list
        boolean isNewCategoryPresent = returnedCategories.incomes().stream()
                .anyMatch(category -> "bonusSalary".equals(category.name()));

        assertTrue(isNewCategoryPresent, "Expected the new category to be present, but it was not found in the returned list");
    }


    @Test
    void testDeleteCategory() throws Exception {
        int categoryIdToDelete = userCategoryRepository.save(TestData.returnDefaultCategory()).getId();
        Category categoryToDelete = TestData.returnDefaultCategory();
        categoryToDelete.setId(categoryIdToDelete);
        userCategoryRepository.saveCategoryToUser(savedUser.getId(), categoryToDelete);

        RequestBuilder deleteRequest = MockMvcRequestBuilders.delete("/user-categories/" + categoryIdToDelete)
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token);

        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());

        // Get all categories to ensure the deleted one is missing
        RequestBuilder getRequest = MockMvcRequestBuilders.get("/user-categories")
                .header("Authorization", "Bearer " + token)
                .requestAttr("jwtToken", token);

        MvcResult mvcResult = mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        CategorySettingsDTO returnedCategories = TestUtils.OBJECT_MAPPER.readValue(responseContent, CategorySettingsDTO.class);

        // Assert the category is not present in the returned list
        boolean isCategoryPresent = returnedCategories.incomes().stream()
                .anyMatch(category -> category.id() == categoryIdToDelete);

        assertFalse(isCategoryPresent, "Expected the category to be deleted, but it was found in the returned list");
    }
}