package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.CategoryDTO;
import com.nagarro.si.pba.dto.CategorySettingsDTO;
import com.nagarro.si.pba.mapper.CategoryMapper;
import com.nagarro.si.pba.model.Category;
import com.nagarro.si.pba.model.CategoryType;
import com.nagarro.si.pba.repository.UserCategoryRepository;
import com.nagarro.si.pba.service.impl.UserCategoryServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCategoryServiceTest {
    @InjectMocks
    private UserCategoryServiceImpl userCategoryService;

    @Mock
    private UserCategoryRepository userCategoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    private int userId = 1;

    @Test
    void testGetCategories() {
        List<Category> incomeCategories = TestData.returnListOfIncomeCategories();
        List<Category> expenseCategories = TestData.returnListOfExpenseCategories();
        List<CategoryDTO> incomeCategoryDTOs = TestData.returnListOfIncomeCategoryDTOs();
        List<CategoryDTO> expenseCategoryDTOs = TestData.returnListOfExpenseCategoryDTOs();
        when(userCategoryRepository.findAllByTypeFromCategoryAndUserCategories(userId, CategoryType.INCOME)).thenReturn(incomeCategories);
        when(userCategoryRepository.findAllByTypeFromCategoryAndUserCategories(userId, CategoryType.EXPENSE)).thenReturn(expenseCategories);
        when(categoryMapper.entityToDTO(incomeCategories)).thenReturn(incomeCategoryDTOs);
        when(categoryMapper.entityToDTO(expenseCategories)).thenReturn(expenseCategoryDTOs);

        CategorySettingsDTO categorySettingsDTO = userCategoryService.getCategories(userId);

        assertNotNull(categorySettingsDTO);
        assertEquals(incomeCategoryDTOs, categorySettingsDTO.incomes());
        assertEquals(expenseCategoryDTOs, categorySettingsDTO.expenses());
    }

    @Test
    void testUpdate() {
        CategorySettingsDTO categorySettingsDTO = TestData.returnCategorySettingsDTO();
        List<CategoryDTO> existingIncomes = TestData.returnListOfIncomeCategoryDTOs();
        List<CategoryDTO> existingExpenses = TestData.returnListOfExpenseCategoryDTOs();
        List<CategoryDTO> updatedIncomes = TestData.returnListOfIncomeCategoryDTOs();
        List<CategoryDTO> updatedExpenses = TestData.returnListOfExpenseCategoryDTOs();
        when(categoryMapper.entityToDTO(userCategoryRepository.findAllByTypeFromUserCategories(userId, CategoryType.INCOME))).thenReturn(existingIncomes);
        when(categoryMapper.entityToDTO(userCategoryRepository.findAllByTypeFromUserCategories(userId, CategoryType.EXPENSE))).thenReturn(existingExpenses);
        when(categoryMapper.entityToDTO(userCategoryRepository.findAllByTypeFromUserCategories(userId, CategoryType.INCOME))).thenReturn(updatedIncomes);
        when(categoryMapper.entityToDTO(userCategoryRepository.findAllByTypeFromUserCategories(userId, CategoryType.EXPENSE))).thenReturn(updatedExpenses);

        CategorySettingsDTO updatedCategorySettingsDTO = userCategoryService.update(userId, categorySettingsDTO);

        assertNotNull(updatedCategorySettingsDTO);
        assertEquals(updatedIncomes.size(), updatedCategorySettingsDTO.incomes().size());
        assertEquals(updatedExpenses.size(), updatedCategorySettingsDTO.expenses().size());
    }

    @Test
    void testDeleteCategory() {
        int categoryId = 100;

        userCategoryService.delete(userId, categoryId);

        verify(userCategoryRepository).delete(userId, categoryId);
    }
}