package com.nagarro.si.pba.utils;

import com.nagarro.si.pba.model.RepetitionFlow;
import com.nagarro.si.pba.model.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionUtils {

    private TransactionUtils() {
        // private constructor to prevent instantiation
    }

    public static boolean checkTransactionDate(Transaction transaction) {
        LocalDate currentDate = LocalDateTime.now().toLocalDate();
        return transaction.getAddedDate().toLocalDate().equals(currentDate);
    }


    public static LocalDateTime getNextDate(LocalDateTime currentDate, RepetitionFlow repetitionFlow) {
        return switch (repetitionFlow) {
            case DAILY -> currentDate.plusDays(1);
            case WEEKLY -> currentDate.plusWeeks(1);
            case MONTHLY -> currentDate.plusMonths(1);
            case ANNUALLY -> currentDate.plusYears(1);
            default -> throw new IllegalArgumentException("Unknown repetition flow: " + repetitionFlow);
        };
    }
}
