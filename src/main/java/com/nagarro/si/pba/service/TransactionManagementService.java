package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.TransactionDTO;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.Income;

public interface TransactionManagementService {
    void addIncomeTransactionToUser(int userId, Income income);
    void addIncomeTransactionToGroup(int groupId, Income income);
    void addExpenseTransactionToUser(int userId, Expense expense);
    void addExpenseTransactionToGroup(int groupId, Expense expense);
    void handleIncomeForUserOrGroup(Income income, TransactionDTO transactionDTO, int userId);
    void handleExpenseForUserOrGroup(Expense expense, TransactionDTO transactionDTO, int userId);

}


