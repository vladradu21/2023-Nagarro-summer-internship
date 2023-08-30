package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.CategorySettingsDTO;
import org.springframework.stereotype.Service;

@Service
public interface GroupCategoryService {
    CategorySettingsDTO getCategories(int groupId);

    CategorySettingsDTO update(int groupId, CategorySettingsDTO categorySettingsDTO);

    void delete(int groupId, int categoryId);
}