package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Category;
import com.nagarro.si.pba.model.CategoryType;
import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.Group;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("inttest")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:script/cleanup.sql")
class GroupCategoryRepositoryTest extends AbstractMySQLContainer {
    @Autowired
    private GroupCategoryRepository groupCategoryRepository;

    @Autowired
    private GroupRepository groupRepository;

    private Group group;
    private Category nonDefaultCategory;

    @BeforeEach
    public void setup() {
        group = groupRepository.save(new Group(null, "DonJoeGroup", Currency.RON));
        nonDefaultCategory = groupCategoryRepository.save(TestData.returnNonDefaultCategory());
        groupCategoryRepository.saveCategoryToGroup(group.getId(), nonDefaultCategory);
    }

    @Test
    void testSaveCategory() {
        Category newCategory = TestData.returnNewCategory();
        Category savedCategory = groupCategoryRepository.save(newCategory);
        Category savedToUserCategories = groupCategoryRepository.saveCategoryToGroup(group.getId(), savedCategory);

        assertNotEquals(0, savedCategory.getId());
        assertEquals("shopping", savedCategory.getName());
        assertEquals(CategoryType.EXPENSE, savedCategory.getType());
        assertFalse(savedCategory.getIsDefault());
        assertTrue(savedCategory.getIsSelected());

        assertNotEquals(0, savedToUserCategories.getId());
        Optional<Category> findCategory = groupCategoryRepository.findByIds(group.getId(), savedCategory.getId());
        assertTrue(findCategory.isPresent());
    }

    @Test
    void testUpdate() {
        Category categoryToUpdate = TestData.returnNewCategory();
        categoryToUpdate.setId(nonDefaultCategory.getId());

        Category updatedCategory = groupCategoryRepository.update(group.getId(), categoryToUpdate);

        assertEquals(updatedCategory.getId(), categoryToUpdate.getId());
        assertEquals(updatedCategory.getName(), categoryToUpdate.getName());
        Optional<Category> getCategoryFromUser = groupCategoryRepository.findByIds(group.getId(), nonDefaultCategory.getId());
        getCategoryFromUser.ifPresent(category -> assertEquals(updatedCategory.getName(), category.getName()));
    }

    @Test
    void testDeleteNonDefaultCategory() {
        groupCategoryRepository.delete(group.getId(), nonDefaultCategory.getId());
        Optional<Category> findFromCategory = groupCategoryRepository.findById(nonDefaultCategory.getId());
        Optional<Category> findFromUserCategories = groupCategoryRepository.findByIds(group.getId(), nonDefaultCategory.getId());

        assertFalse(findFromCategory.isPresent());
        assertFalse(findFromUserCategories.isPresent());
    }

    @Test
    void testFindById() {
        Optional<Category> findFromCategory = groupCategoryRepository.findById(nonDefaultCategory.getId());
        assertTrue(findFromCategory.isPresent());
    }

    @Test
    void testFindByIds() {
        Optional<Category> findFromUserCategories = groupCategoryRepository.findByIds(group.getId(), nonDefaultCategory.getId());
        Optional<Category> findFromUserCategoriesNotFound = groupCategoryRepository.findByIds(group.getId(), 404);

        assertTrue(findFromUserCategories.isPresent());
        assertFalse(findFromUserCategoriesNotFound.isPresent());
    }

    @Test
    void testFindAllIncomes() {
        List<Category> incomes = groupCategoryRepository.findAllByTypeFromCategoryAndGroupCategories(group.getId(), CategoryType.INCOME);
        for (Category income : incomes) {
            assertEquals(CategoryType.INCOME, income.getType());
        }
    }

    @Test
    void testFindAllExpenses() {
        List<Category> expenses = groupCategoryRepository.findAllByTypeFromCategoryAndGroupCategories(group.getId(), CategoryType.EXPENSE);
        for (Category expense : expenses) {
            assertEquals(CategoryType.EXPENSE, expense.getType());
        }
    }
}