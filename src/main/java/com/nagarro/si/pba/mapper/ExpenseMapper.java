package com.nagarro.si.pba.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.nagarro.si.pba.dto.ExpenseDTO;
import com.nagarro.si.pba.model.Expense;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    ExpenseDTO entityToDTO(Expense expense);

    List<ExpenseDTO> entityToDTO(List<Expense> expenses);

    Expense dtoToEntity(ExpenseDTO expenseDTO);

    List<Expense> dtoToEntity(List<ExpenseDTO> expensesDTO);
}
