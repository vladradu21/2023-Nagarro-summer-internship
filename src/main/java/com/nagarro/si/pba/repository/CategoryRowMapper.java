package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Category;
import com.nagarro.si.pba.model.CategoryType;

import org.mapstruct.Mapper;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

@Mapper
public class CategoryRowMapper implements RowMapper<Category> {
    @Override
    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("category_id"));
        category.setName(rs.getString("category_name"));
        category.setType(CategoryType.valueOf(rs.getString("type")));
        category.setIsDefault(rs.getBoolean("isDefault"));
        category.setIsSelected(rs.getBoolean("isSelected"));
        return category;
    }
}

