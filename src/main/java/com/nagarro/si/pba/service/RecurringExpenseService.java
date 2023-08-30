package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.ExpenseDTO;
import org.springframework.stereotype.Service;


@Service
public interface RecurringExpenseService {
    ExpenseDTO saveExpense(ExpenseDTO expenseDTO, int userId);
}
