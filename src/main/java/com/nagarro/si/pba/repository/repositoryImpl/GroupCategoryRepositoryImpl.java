package com.nagarro.si.pba.repository.repositoryImpl;

import com.nagarro.si.pba.exceptions.NoRowsAffectedByDelete;
import com.nagarro.si.pba.model.Category;
import com.nagarro.si.pba.model.CategoryType;
import com.nagarro.si.pba.repository.BaseRepository;
import com.nagarro.si.pba.repository.CategoryRowMapper;
import com.nagarro.si.pba.repository.GroupCategoryRepository;
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
public class GroupCategoryRepositoryImpl extends BaseRepository implements GroupCategoryRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupCategoryRepositoryImpl.class);

    @Autowired
    public GroupCategoryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Category save(Category category) {
        LOGGER.info("Saving category: {}", category.getName());
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
    public Category update(int groupId, Category category) {
        LOGGER.info("Updating category with ID: {} for group with ID: {}", category.getId(), groupId);

        // Update the category table
        String categoryUpdateQuery = "UPDATE category \n" +
                "SET name = ?, type = ?\n" +
                "WHERE id = ?";
        jdbcTemplate.update(
                categoryUpdateQuery,
                category.getName(),
                category.getType().toString(),
                category.getId());

        // Update the group_categories table
        String groupCategoriesUpdateQuery = """
                UPDATE group_categories\s
                SET category_name = ?, type = ?, isSelected =?
                WHERE group_id = ? AND category_id = ?""";
        jdbcTemplate.update(
                groupCategoriesUpdateQuery,
                category.getName(),
                category.getType().toString(),
                category.getIsSelected(),
                groupId,
                category.getId());

        return category;
    }

    @Override
    public void delete(int groupId, int categoryId) {
        LOGGER.warn("Deleting category with ID: {} for group with ID: {}", categoryId, groupId);

        // Retrieve the isDefault value from the category table
        String getCategoryQuery = "SELECT isDefault FROM category WHERE id = ?";
        boolean isDefault = Boolean.TRUE.equals(jdbcTemplate.query(
                getCategoryQuery,
                rs -> rs.next() && rs.getBoolean(1),
                categoryId));

        // Delete the row from group_categories table
        String groupCategoriesDeleteQuery = "DELETE FROM group_categories WHERE group_id = ? AND category_id = ?";
        int rowsAffected = jdbcTemplate.update(groupCategoriesDeleteQuery, groupId, categoryId);

        if (rowsAffected == 0) {
            throw new NoRowsAffectedByDelete("No rows affected when trying to delete category with groupId: " + groupId + " and categoryId: " + categoryId);
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
    public Optional<Category> findByIds(int groupId, int categoryId) {
        LOGGER.info("Finding category by groupId: {} and categoryId: {}", groupId, categoryId);
        String query = "SELECT * FROM group_categories WHERE group_id = ? AND category_id = ?";
        List<Category> result = jdbcTemplate.query(query,
                new CategoryRowMapper(),
                groupId, categoryId);
        return result.stream().findFirst();
    }

    @Override
    public Category saveCategoryToGroup(int groupId, Category category) {
        LOGGER.info("Saving category to group with ID: {}", groupId);
        String query = "insert into group_categories (group_id, category_id, category_name, type, isDefault, isSelected) values (?,?,?,?,?,?)";
        jdbcTemplate.update(query, groupId, category.getId(), category.getName(), category.getType().toString(), category.getIsDefault(), category.getIsSelected());

        return category;
    }

    @Override
    public List<Category> findAllByTypeFromCategoryAndGroupCategories(int groupId, CategoryType categoryType) {
        LOGGER.info("Finding categories by groupId: {} and categoryType: {} from group_categories and category", groupId, categoryType);
        String query = "SELECT gc.group_id, gc.category_id, gc.category_name, gc.type, c.isDefault, gc.isSelected " +
                "FROM group_categories gc " +
                "LEFT JOIN category c ON gc.category_id = c.id " +
                "WHERE gc.group_id = ? AND gc.type = ? " +
                "UNION " +
                "SELECT id as group_id, id as category_id, name as category_name, type, isDefault, 0 as isSelected " +
                "FROM category " +
                "WHERE type = ? AND id NOT IN ( " +
                "    SELECT category_id " +
                "    FROM group_categories " +
                "    WHERE group_id = ? AND type = ? " +
                ")";

        return jdbcTemplate.query(query, new CategoryRowMapper(), groupId, categoryType.toString(), categoryType.toString(), groupId, categoryType.toString());
    }

    @Override
    public List<Category> findAllByTypeFromGroupCategories(int groupId, CategoryType categoryType) {
        LOGGER.info("Finding categories by groupId: {} and categoryType: {} from group_categories", groupId, categoryType);
        String query = "SELECT * FROM group_categories WHERE group_id = ? AND type = ?";
        return jdbcTemplate.query(query, new CategoryRowMapper(), groupId, categoryType.toString());
    }
}