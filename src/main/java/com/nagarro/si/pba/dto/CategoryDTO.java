package com.nagarro.si.pba.dto;

import com.nagarro.si.pba.model.CategoryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record CategoryDTO(
        Integer id,
        @NotNull
        @Size(min = 4, max = 20)
        String name,
        @NotNull
        CategoryType type,

        boolean isDefault,

        boolean isSelected
) {
        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                CategoryDTO that = (CategoryDTO) o;
                return isDefault == that.isDefault && isSelected == that.isSelected && Objects.equals(id, that.id) && Objects.equals(name, that.name) && type == that.type;
        }

        @Override
        public int hashCode() {
                return Objects.hash(id, name, type, isDefault, isSelected);
        }
}
