package com.nagarro.si.pba.mapper;

import com.nagarro.si.pba.dto.TransactionDTO;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    List<TransactionDTO> entityToDto(List<Transaction> transactionList);
    TransactionDTO incomeEntityToDTO(Income income);
    TransactionDTO expenseEntityToDTO(Expense expense);
    Income dtoToIncomeEntity(TransactionDTO transactionDTO);
    Expense dtoToExpenseEntity(TransactionDTO transactionDTO);
}
