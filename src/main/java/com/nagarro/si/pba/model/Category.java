package com.nagarro.si.pba.model;

import org.springframework.data.annotation.Id;

import java.util.Objects;

public class Category {
    @Id
    private int id;

    private String name;

    private CategoryType type;

    private boolean isDefault;

    private boolean isSelected;

    public Category() {
    }

    public Category(int id, String name, CategoryType type, boolean isDefault, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isDefault = isDefault;
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", isDefault=" + isDefault +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id && isDefault == category.isDefault && Objects.equals(name, category.name) && type == category.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, isDefault);
    }
}