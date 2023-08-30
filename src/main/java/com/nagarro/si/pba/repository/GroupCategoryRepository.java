package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Category;
import com.nagarro.si.pba.model.CategoryType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupCategoryRepository {
    Category save(Category category);

    Category update(int groupId, Category category);

    void delete(int groupId, int categoryId);

    Optional<Category> findById(int categoryId);

    Optional<Category> findByIds(int groupId, int categoryId);

    Category saveCategoryToGroup(int groupId, Category category);

    List<Category> findAllByTypeFromCategoryAndGroupCategories(int groupId, CategoryType categoryType);

    List<Category> findAllByTypeFromGroupCategories(int groupId, CategoryType categoryType);
}