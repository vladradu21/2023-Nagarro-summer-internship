package com.nagarro.si.pba.mapper;

import com.nagarro.si.pba.dto.CategoryDTO;
import com.nagarro.si.pba.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    CategoryDTO entityToDTO(Category category);

    List<CategoryDTO> entityToDTO(List<Category> categories);

    Category dtoToEntity(CategoryDTO categoryDTO);

    List<Category> dtoToEntity(List<CategoryDTO> categoryDTOS);
}
