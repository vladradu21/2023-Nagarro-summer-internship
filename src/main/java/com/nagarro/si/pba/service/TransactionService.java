package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.TransactionDTO;
import com.nagarro.si.pba.dto.TransactionFilterDTO;
import com.nagarro.si.pba.model.ReportType;
import com.nagarro.si.pba.model.Transaction;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    List<TransactionDTO> getAllTransactions(TransactionFilterDTO transactionFilterDTO, int userId);
    void addTransaction(TransactionDTO transactionDTO, int userId);
    public void processRecurringTransactions();
    List<Transaction> getBalancedTransactions(LocalDateTime startDate, LocalDateTime endDate, ReportType reportType, int userId);
}
