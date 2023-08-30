package com.nagarro.si.pba.dto;

import java.util.List;

public record CategorySettingsDTO(
        List<CategoryDTO> incomes,
        List<CategoryDTO> expenses
) {
}
