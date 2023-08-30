package com.nagarro.si.pba.dto;

import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.RepetitionFlow;
import com.nagarro.si.pba.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TransactionDTO(
        Integer id,

        @NotNull
        TransactionType type,

        @NotNull
        String category,

        @NotNull
        String name,

        @DecimalMin(value = "0.01", message = "The amount must be positive")
        double amount,

        String description,

        @NotNull
        Currency currency,

        @NotNull
        LocalDateTime addedDate,

        @NotNull
        RepetitionFlow repetitionFlow,

        Integer groupId
) {
}