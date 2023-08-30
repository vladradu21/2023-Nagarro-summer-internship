package com.nagarro.si.pba.service;

import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.Income;

import java.util.List;

public interface RecurringTransactionService {
    List<Income> getRecurringIncomeTransactions();

    List<Expense> getRecurringExpenseTransactions();
}
