package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Category;
import com.nagarro.si.pba.model.CategoryType;
import com.nagarro.si.pba.model.User;
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
class UserCategoryRepositoryTest extends AbstractMySQLContainer {
    @Autowired
    private UserCategoryRepository userCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Category nonDefaultCategory;

    @BeforeEach
    public void setup() {
        user = TestData.returnUserForDonJoe();
        userRepository.save(user);

        nonDefaultCategory = userCategoryRepository.save(TestData.returnNonDefaultCategory());
        userCategoryRepository.saveCategoryToUser(user.getId(), nonDefaultCategory);
    }

    @Test
    void testSaveCategory() {
        Category newCategory = TestData.returnNewCategory();
        Category savedCategory = userCategoryRepository.save(newCategory);
        Category savedToUserCategories = userCategoryRepository.saveCategoryToUser(user.getId(), savedCategory);

        assertNotEquals(0, savedCategory.getId());
        assertEquals("shopping", savedCategory.getName());
        assertEquals(CategoryType.EXPENSE, savedCategory.getType());
        assertFalse(savedCategory.getIsDefault());
        assertTrue(savedCategory.getIsSelected());

        assertNotEquals(0, savedToUserCategories.getId());
        Optional<Category> findCategory = userCategoryRepository.findByIds(user.getId(), savedCategory.getId());
        assertTrue(findCategory.isPresent());
    }

    @Test
    void testUpdate() {
        Category categoryToUpdate = TestData.returnNewCategory();
        categoryToUpdate.setId(nonDefaultCategory.getId());

        Category updatedCategory = userCategoryRepository.update(user.getId(), categoryToUpdate);

        assertEquals(updatedCategory.getId(), categoryToUpdate.getId());
        assertEquals(updatedCategory.getName(), categoryToUpdate.getName());
        Optional<Category> getCategoryFromUser = userCategoryRepository.findByIds(user.getId(), nonDefaultCategory.getId());
        getCategoryFromUser.ifPresent(category -> assertEquals(updatedCategory.getName(), category.getName()));
    }

    @Test
    void testDeleteNonDefaultCategory() {
        userCategoryRepository.delete(user.getId(), nonDefaultCategory.getId());
        Optional<Category> findFromCategory = userCategoryRepository.findById(nonDefaultCategory.getId());
        Optional<Category> findFromUserCategories = userCategoryRepository.findByIds(user.getId(), nonDefaultCategory.getId());

        assertFalse(findFromCategory.isPresent());
        assertFalse(findFromUserCategories.isPresent());
    }

    @Test
    void testFindById() {
        Optional<Category> findFromCategory = userCategoryRepository.findById(nonDefaultCategory.getId());
        assertTrue(findFromCategory.isPresent());
    }

    @Test
    void testFindByIds() {
        Optional<Category> findFromUserCategories = userCategoryRepository.findByIds(user.getId(), nonDefaultCategory.getId());
        Optional<Category> findFromUserCategoriesNotFound = userCategoryRepository.findByIds(user.getId(), 404);

        assertTrue(findFromUserCategories.isPresent());
        assertFalse(findFromUserCategoriesNotFound.isPresent());
    }

    @Test
    void testFindAllIncomes() {
        List<Category> incomes = userCategoryRepository.findAllByTypeFromCategoryAndUserCategories(user.getId(), CategoryType.INCOME);
        for (Category income : incomes) {
            assertEquals(CategoryType.INCOME, income.getType());
        }
    }

    @Test
    void testFindAllExpenses() {
        List<Category> expenses = userCategoryRepository.findAllByTypeFromCategoryAndUserCategories(user.getId(), CategoryType.EXPENSE);
        for (Category expense : expenses) {
            assertEquals(CategoryType.EXPENSE, expense.getType());
        }
    }
}