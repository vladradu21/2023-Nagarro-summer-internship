package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Category;
import com.nagarro.si.pba.model.CategoryType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCategoryRepository {
    Category save(Category category);

    Category update(int userId, Category category);

    void delete(int userId, int categoryId);

    Optional<Category> findById(int categoryId);

    Optional<Category> findByIds(int userId, int categoryId);

    Category saveCategoryToUser(int userId, Category category);

    List<Category> findAllByTypeFromCategoryAndUserCategories(int userId, CategoryType categoryType);

    List<Category> findAllByTypeFromUserCategories(int userId, CategoryType categoryType);
}