package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.CategorySettingsDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserCategoryService {
    CategorySettingsDTO getCategories(int userId);

    CategorySettingsDTO update(int userId, CategorySettingsDTO categorySettingsDTO);

    void delete(int userId, int categoryId);

}