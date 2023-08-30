package com.nagarro.si.pba.dto;

import com.nagarro.si.pba.exceptions.InvalidInputException;

import java.time.LocalDateTime;

public record TransactionFilterDTO(
        LocalDateTime exactDate,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
    public TransactionFilterDTO {
        if (exactDate != null && (startDate != null || endDate != null)) {
            throw new InvalidInputException("Invalid filter combination: exactDate and rangeDate should not be set together.");
        }
    }
}