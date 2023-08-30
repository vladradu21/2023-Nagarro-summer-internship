package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.dto.CategoryDTO;
import com.nagarro.si.pba.dto.CategorySettingsDTO;
import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.mapper.CategoryMapper;
import com.nagarro.si.pba.model.Category;
import com.nagarro.si.pba.model.CategoryType;
import com.nagarro.si.pba.repository.UserCategoryRepository;
import com.nagarro.si.pba.service.UserCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserCategoryServiceImpl implements UserCategoryService {
    private final UserCategoryRepository userCategoryRepository;
    private final CategoryMapper categoryMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCategoryServiceImpl.class);

    @Autowired
    public UserCategoryServiceImpl(UserCategoryRepository userCategoryRepository, CategoryMapper categoryMapper) {
        this.userCategoryRepository = userCategoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategorySettingsDTO getCategories(int userId) {
        LOGGER.info("Fetching categories for user with ID: {}", userId);
        List<CategoryDTO> existingIncomes = categoryMapper.entityToDTO(userCategoryRepository.findAllByTypeFromCategoryAndUserCategories(userId, CategoryType.INCOME));
        List<CategoryDTO> existingExpenses = categoryMapper.entityToDTO(userCategoryRepository.findAllByTypeFromCategoryAndUserCategories(userId, CategoryType.EXPENSE));

        return new CategorySettingsDTO(existingIncomes, existingExpenses);
    }

    @Override
    public CategorySettingsDTO update(int userId, CategorySettingsDTO categorySettingsDTO) {
        LOGGER.info("Updating categories for user with ID: {}", userId);
        List<CategoryDTO> existingIncomes = categoryMapper.entityToDTO(userCategoryRepository.findAllByTypeFromUserCategories(userId, CategoryType.INCOME));
        List<CategoryDTO> existingExpenses = categoryMapper.entityToDTO(userCategoryRepository.findAllByTypeFromUserCategories(userId, CategoryType.EXPENSE));

        for (CategoryDTO incomeDTO : categorySettingsDTO.incomes()) {
            handleCategory(userId, incomeDTO, existingIncomes);
        }

        for (CategoryDTO expenseDTO : categorySettingsDTO.expenses()) {
            handleCategory(userId, expenseDTO, existingExpenses);
        }

        List<CategoryDTO> userIncomes = categoryMapper.entityToDTO(userCategoryRepository.findAllByTypeFromUserCategories(userId, CategoryType.INCOME));
        List<CategoryDTO> userExpenses = categoryMapper.entityToDTO(userCategoryRepository.findAllByTypeFromUserCategories(userId, CategoryType.EXPENSE));
        LOGGER.info("Categories updated for user with ID: {}", userId);
        return new CategorySettingsDTO(userIncomes, userExpenses);
    }

    private void handleCategory(int userId, CategoryDTO categoryDTO, List<CategoryDTO> existingCategories) {
        LOGGER.debug("Handling category with ID: {}", categoryDTO.id());
        Optional<CategoryDTO> existingCategory = existingCategories.stream()
                .filter(existing -> Objects.equals(existing.id(), categoryDTO.id()))
                .findFirst();

        if (existingCategory.isPresent()) {
            CategoryDTO existingCategoryDTO = existingCategory.get();

            if(!existingCategoryDTO.equals(categoryDTO)) {
                LOGGER.info("Updating category with ID: {} for user with ID: {}", categoryDTO.id(), userId);
                updateCategory(userId, categoryDTO);
            }
        } else {
            if (categoryDTO.id() == null) {
                LOGGER.info("Adding new category for user with ID: {}", userId);
                addCategory(userId, categoryDTO);
            } else {
                LOGGER.info("Adding unselected category to user's categories for user with ID: {}", userId);
                addOnlyToUserCategories(userId, categoryDTO);
            }
        }
    }

    private void addCategory(int userId, CategoryDTO categoryDTO) {
        Category categoryToSave = categoryMapper.dtoToEntity(categoryDTO);
        userCategoryRepository.save(categoryToSave);

        if(categoryDTO.isSelected()) {
            userCategoryRepository.saveCategoryToUser(userId, categoryToSave);
            LOGGER.info("Added new selected category to user's categories for user with ID: {}", userId);
        }
    }

    private void addOnlyToUserCategories(int userId, CategoryDTO categoryDTO) {
        Category categoryToSave = categoryMapper.dtoToEntity(categoryDTO);

        if(categoryDTO.isSelected()) {
            userCategoryRepository.saveCategoryToUser(userId, categoryToSave);
            LOGGER.info("Added selected category with ID: {} to user's categories for user with ID: {}", categoryDTO.id(), userId);
        }
    }

    private void updateCategory(int userId, CategoryDTO updateCategory) {
        Category categoryToUpdate = userCategoryRepository.findByIds(userId, updateCategory.id())
                .orElseThrow(() -> {
                    LOGGER.error("Category with ID: {} not found for user with ID: {}", updateCategory.id(), userId);
                    return new PbaNotFoundException(ExceptionMessage.CATEGORY_NOT_FOUND.format(updateCategory.id()));
                });

        categoryToUpdate.setIsSelected(updateCategory.isSelected());
        userCategoryRepository.update(userId, categoryToUpdate);
        LOGGER.info("Updated category with ID: {} for user with ID: {}", updateCategory.id(), userId);
    }

    @Override
    public void delete(int userId, int categoryId) {
        LOGGER.warn("Deleting category with ID: {} for user with ID: {}", categoryId, userId);
        userCategoryRepository.delete(userId, categoryId);
    }
}