package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.dto.TransactionFilterDTO;
import com.nagarro.si.pba.model.Transaction;

import java.util.List;

public interface TransactionRepository {
    List<Transaction> getAll(TransactionFilterDTO transactionFilterDTO, int userId);
}
