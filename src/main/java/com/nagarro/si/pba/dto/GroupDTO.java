package com.nagarro.si.pba.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

import com.nagarro.si.pba.model.Currency;

public record GroupDTO(
        Integer id,

        @NotNull
        String name,

        @NotNull
        Currency defaultCurrency) {
}
