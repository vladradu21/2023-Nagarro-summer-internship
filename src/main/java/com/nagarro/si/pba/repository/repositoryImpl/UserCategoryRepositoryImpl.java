package com.nagarro.si.pba.repository.repositoryImpl;

import com.nagarro.si.pba.exceptions.NoRowsAffectedByDelete;
import com.nagarro.si.pba.model.Category;
import com.nagarro.si.pba.model.CategoryType;
import com.nagarro.si.pba.repository.BaseRepository;
import com.nagarro.si.pba.repository.CategoryRowMapper;
import com.nagarro.si.pba.repository.UserCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserCategoryRepositoryImpl extends BaseRepository implements UserCategoryRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCategoryRepositoryImpl.class);

    @Autowired
    public UserCategoryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Category save(Category category) {
        LOGGER.info("Saving new category: {}", category.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "insert into category (name, type) values (?,?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, category.getName());
            ps.setString(2, category.getType().toString());
            return ps;
        }, keyHolder);

        int generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        category.setId(generatedId);

        LOGGER.info("Saved category: {} with ID: {}", category.getName(), category.getId());
        return category;
    }

    @Override
    public Category update(int userId, Category category) {
        LOGGER.info("Updating category with ID: {}", category.getId());

        // Update the category table
        String categoryUpdateQuery = "UPDATE category \n" +
                "SET name = ?, type = ?\n" +
                "WHERE id = ?";
        jdbcTemplate.update(
                categoryUpdateQuery,
                category.getName(),
                category.getType().toString(),
                category.getId());

        // Update the user_categories table
        String userCategoriesUpdateQuery = "UPDATE user_categories \n" +
                "SET category_name = ?, type = ?, isSelected =?\n" +
                "WHERE user_id = ? AND category_id = ?";
        jdbcTemplate.update(
                userCategoriesUpdateQuery,
                category.getName(),
                category.getType().toString(),
                category.getIsSelected(),
                userId,
                category.getId());

        return category;
    }

    @Override
    public void delete(int userId, int categoryId) {
        LOGGER.warn("Deleting category with userId: {} and categoryId: {}", userId, categoryId);

        // Retrieve the isDefault value from the category table
        String getCategoryQuery = "SELECT isDefault FROM category WHERE id = ?";
        boolean isDefault = Boolean.TRUE.equals(jdbcTemplate.queryForObject(getCategoryQuery, Boolean.class, categoryId));

        // Delete the row from user_categories table
        String userCategoriesDeleteQuery = "DELETE FROM user_categories WHERE user_id = ? AND category_id = ?";
        int rowsAffected = jdbcTemplate.update(userCategoriesDeleteQuery, userId, categoryId);

        if (rowsAffected == 0) {
            throw new NoRowsAffectedByDelete("No rows affected when trying to delete category with userId: " + userId + " and categoryId: " + categoryId);
        }

        // Check if the category is not the default category before deleting from the category table
        if (!isDefault) {
            String categoryDeleteQuery = "DELETE FROM category WHERE id = ?";
            jdbcTemplate.update(categoryDeleteQuery, categoryId);
        }
    }

    @Override
    public Optional<Category> findById(int categoryId) {
        LOGGER.info("Finding category by ID: {}", categoryId);
        String query = "SELECT * FROM category WHERE id = ?";
        List<Category> result = jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(Category.class),
                categoryId);
        return result.stream().findFirst();
    }

    @Override
    public Optional<Category> findByIds(int userId, int categoryId) {
        LOGGER.info("Finding category by userId: {} and categoryId: {}", userId, categoryId);
        String query = "SELECT * FROM user_categories WHERE user_id = ? AND category_id = ?";
        List<Category> result = jdbcTemplate.query(query,
                new CategoryRowMapper(),
                userId, categoryId);
        return result.stream().findFirst();
    }

    @Override
    public Category saveCategoryToUser(int userId, Category category) {
        LOGGER.info("Saving category to user with ID: {}", userId);
        String query = "insert into user_categories (user_id, category_id, category_name, type, isDefault, isSelected) values (?,?,?,?,?,?)";
        jdbcTemplate.update(query, userId, category.getId(), category.getName(), category.getType().toString(),category.getIsDefault(), category.getIsSelected());

        return category;
    }

    @Override
    public List<Category> findAllByTypeFromCategoryAndUserCategories(int userId, CategoryType categoryType) {
        LOGGER.info("Finding categories by userId: {} and categoryType: {} from user_categories and category", userId, categoryType);
        String query = "SELECT user_id, category_id, category_name, type, isDefault, isSelected " +
                "FROM user_categories " +
                "WHERE user_id = ? AND type = ? " +
                "UNION " +
                "SELECT id as user_id, id as category_id, name as category_name, type, isDefault, 0 as isSelected " +
                "FROM category " +
                "WHERE type = ? AND id NOT IN ( " +
                "    SELECT category_id " +
                "    FROM user_categories " +
                "    WHERE user_id = ? AND type = ? " +
                ")";

        return jdbcTemplate.query(query, new CategoryRowMapper(), userId, categoryType.toString(), categoryType.toString(), userId, categoryType.toString());
    }

    @Override
    public List<Category> findAllByTypeFromUserCategories(int userId, CategoryType categoryType) {
        LOGGER.info("Finding categories by userId: {} and categoryType: {} from user_categories", userId, categoryType);
        String query = "SELECT * FROM user_categories WHERE type = ?";
        return jdbcTemplate.query(query, new CategoryRowMapper(), categoryType.toString());
    }
}