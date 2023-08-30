package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.ExpenseDTO;
import com.nagarro.si.pba.exceptions.PbaConflictException;
import com.nagarro.si.pba.mapper.ExpenseMapper;
import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.repository.ExpenseRepository;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.service.impl.RecurringExpenseServiceImpl;
import com.nagarro.si.pba.utils.TestData;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RecurringExpenseServiceTest {
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private ExpenseMapper expenseMapper;
    @Mock
    private BnrCurrencyService bnrCurrencyService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RecurringExpenseServiceImpl recurringExpenseService;

    @Test
    public void testSaveExpense() {
        ExpenseDTO expenseDTO = TestData.returnRecurringExpenseDTO();
        Expense expense = TestData.returnRecurringExpenseEntity();
        ExpenseDTO savedExpenseDTO = TestData.returnOneTimeExpenseDTO();

        when(expenseMapper.dtoToEntity(expenseDTO)).thenReturn(expense);
        when(expenseMapper.entityToDTO(expense)).thenReturn(savedExpenseDTO);
        when(expenseRepository.save(expense, 1)).thenReturn(expense);    
        when(userRepository.getDefaultCurrency(1)).thenReturn(Currency.RON);

        assertSame(savedExpenseDTO, recurringExpenseService.saveExpense(expenseDTO, 1));
    }

    @Test
    public void testOneTimeExpenseInsteadOfRecurringOne() {
        ExpenseDTO expenseDTO = TestData.returnOneTimeExpenseDTO();

        Assertions.assertThrows(PbaConflictException.class, () -> recurringExpenseService.saveExpense(expenseDTO, 1));
    }
}
