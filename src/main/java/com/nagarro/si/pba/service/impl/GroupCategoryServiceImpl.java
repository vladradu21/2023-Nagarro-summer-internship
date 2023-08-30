package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.dto.CategoryDTO;
import com.nagarro.si.pba.dto.CategorySettingsDTO;
import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.mapper.CategoryMapper;
import com.nagarro.si.pba.model.Category;
import com.nagarro.si.pba.model.CategoryType;
import com.nagarro.si.pba.repository.GroupCategoryRepository;
import com.nagarro.si.pba.service.GroupCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GroupCategoryServiceImpl implements GroupCategoryService {
    private final GroupCategoryRepository groupCategoryRepository;
    private final CategoryMapper categoryMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupCategoryServiceImpl.class);

    @Autowired
    public GroupCategoryServiceImpl(GroupCategoryRepository groupCategoryRepository, CategoryMapper categoryMapper) {
        this.groupCategoryRepository = groupCategoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategorySettingsDTO getCategories(int groupId) {
        LOGGER.info("Fetching categories for group with ID: {}", groupId);
        List<CategoryDTO> existingIncomes = categoryMapper.entityToDTO(groupCategoryRepository.findAllByTypeFromCategoryAndGroupCategories(groupId, CategoryType.INCOME));
        List<CategoryDTO> existingExpenses = categoryMapper.entityToDTO(groupCategoryRepository.findAllByTypeFromCategoryAndGroupCategories(groupId, CategoryType.EXPENSE));

        return new CategorySettingsDTO(existingIncomes, existingExpenses);
    }

    @Override
    public CategorySettingsDTO update(int groupId, CategorySettingsDTO categorySettingsDTO) {
        LOGGER.info("Updating categories for group with ID: {}", groupId);
        List<CategoryDTO> existingIncomes = categoryMapper.entityToDTO(groupCategoryRepository.findAllByTypeFromGroupCategories(groupId, CategoryType.INCOME));
        List<CategoryDTO> existingExpenses = categoryMapper.entityToDTO(groupCategoryRepository.findAllByTypeFromGroupCategories(groupId, CategoryType.EXPENSE));

        for (CategoryDTO incomeDTO : categorySettingsDTO.incomes()) {
            handleCategory(groupId, incomeDTO, existingIncomes);
        }

        for (CategoryDTO expenseDTO : categorySettingsDTO.expenses()) {
            handleCategory(groupId, expenseDTO, existingExpenses);
        }

        List<CategoryDTO> userIncomes = categoryMapper.entityToDTO(groupCategoryRepository.findAllByTypeFromGroupCategories(groupId, CategoryType.INCOME));
        List<CategoryDTO> userExpenses = categoryMapper.entityToDTO(groupCategoryRepository.findAllByTypeFromGroupCategories(groupId, CategoryType.EXPENSE));
        LOGGER.info("Categories updated for group with ID: {}", groupId);
        return new CategorySettingsDTO(userIncomes, userExpenses);
    }

    private void handleCategory(int groupId, CategoryDTO categoryDTO, List<CategoryDTO> existingCategories) {
        LOGGER.debug("Handling category with ID: {}", categoryDTO.id());
        Optional<CategoryDTO> existingCategory = existingCategories.stream()
                .filter(existing -> Objects.equals(existing.id(), categoryDTO.id()))
                .findFirst();

        if (existingCategory.isPresent()) {
            CategoryDTO existingCategoryDTO = existingCategory.get();

            if (!existingCategoryDTO.equals(categoryDTO)) {
                LOGGER.info("Updating category with ID: {} for group with ID: {}", categoryDTO.id(), groupId);
                updateCategory(groupId, categoryDTO);
            }
        } else {
            if (categoryDTO.id() == null) {
                LOGGER.info("Adding new category for group with ID: {}", groupId);
                addCategory(groupId, categoryDTO);
            } else {
                LOGGER.info("Adding unselected category to group's categories for group with ID: {}", groupId);
                addOnlyToGroupCategories(groupId, categoryDTO);
            }
        }
    }

    private void addCategory(int groupId, CategoryDTO categoryDTO) {
        Category categoryToSave = categoryMapper.dtoToEntity(categoryDTO);
        groupCategoryRepository.save(categoryToSave);

        if (categoryDTO.isSelected()) {
            groupCategoryRepository.saveCategoryToGroup(groupId, categoryToSave);
            LOGGER.info("Added new selected category to group's categories for group with ID: {}", groupId);
        }
    }

    private void addOnlyToGroupCategories(int groupId, CategoryDTO categoryDTO) {
        Category categoryToSave = categoryMapper.dtoToEntity(categoryDTO);

        if (categoryDTO.isSelected()) {
            groupCategoryRepository.saveCategoryToGroup(groupId, categoryToSave);
            LOGGER.info("Added selected category with ID: {} to group's categories for group with ID: {}", categoryDTO.id(), groupId);
        }
    }

    private void updateCategory(int groupId, CategoryDTO updateCategory) {
        Category categoryToUpdate = groupCategoryRepository.findByIds(groupId, updateCategory.id())
                .orElseThrow(() -> {
                    LOGGER.warn("Category with ID: {} not found for group with ID: {}", updateCategory.id(), groupId);
                    return new PbaNotFoundException(ExceptionMessage.CATEGORY_NOT_FOUND.format(updateCategory.id()));
                });

        categoryToUpdate.setIsSelected(updateCategory.isSelected());
        groupCategoryRepository.update(groupId, categoryToUpdate);
        LOGGER.info("Updated category with ID: {} for group with ID: {}", updateCategory.id(), groupId);
    }

    @Override
    public void delete(int groupId, int categoryId) {
        LOGGER.warn("Deleting category with ID: {} for group with ID: {}", categoryId, groupId);
        groupCategoryRepository.delete(groupId, categoryId);
    }
}