package com.nagarro.si.pba.model;

import java.time.LocalDateTime;

public class Expense extends Transaction {

    public Expense() {
    }

    public Expense(int id, String category, String name, double amount, String description, Currency currency, LocalDateTime addedDate, RepetitionFlow repetitionFlow, int userId, Integer groupId) {
        setId(id);
        setType(TransactionType.EXPENSE);
        setCategory(category);
        setName(name);
        setAmount(amount);
        setDescription(description);
        setCurrency(currency);
        setAddedDate(addedDate);
        setRepetitionFlow(repetitionFlow);
        setUserId(userId);
        setGroupId(groupId);
    }
}
