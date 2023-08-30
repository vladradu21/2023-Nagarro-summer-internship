package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.RepetitionFlow;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository {
    Optional<Expense> findById(int id);

    List<Expense> findAllUserExpenses(int id);

    List<Expense> findAllGroupExpenses(int id);

    List<Expense> findAll();

    void updateRepetitionFlow(int id, RepetitionFlow repetitionFlow);

    List<Expense> getRecurringExpenseTransactions();

    List<Expense> getAllGroupExpensesForSpecificUser(int userId, int groupId);

    Expense save(Expense expense, int userId);
}