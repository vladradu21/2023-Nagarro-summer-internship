package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.TransactionDTO;
import com.nagarro.si.pba.dto.TransactionFilterDTO;
import com.nagarro.si.pba.mapper.TransactionMapper;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.ReportType;
import com.nagarro.si.pba.model.Transaction;
import com.nagarro.si.pba.model.TransactionType;
import com.nagarro.si.pba.repository.ExpenseRepository;
import com.nagarro.si.pba.repository.IncomeRepository;
import com.nagarro.si.pba.repository.TransactionRepository;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.service.impl.TransactionServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    private final List<Transaction> transactionList = TestData.returnTransactions();
    private final List<TransactionDTO> transactionDTOList = TestData.returnTransactionDTOList();
    private final TransactionFilterDTO transactionFilterDTO = TestData.returnTransactionFilterDTOEmpty();
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IncomeRepository incomeRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private TransactionManagementService transactionManagementService;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void testGetAllNoFilter() {
        int userId = 1;


        when(transactionRepository.getAll(transactionFilterDTO, userId)).thenReturn(transactionList);
        when(transactionMapper.entityToDto(transactionList)).thenReturn(transactionDTOList);

        List<TransactionDTO> actualTransactions = transactionService.getAllTransactions(transactionFilterDTO, userId);
        assertEquals(transactionDTOList, actualTransactions);

    }

    @Test
    void testGetAllByExactDate() {
        List<Transaction> transactionList = TestData.returnTransactions();
        List<TransactionDTO> transactionDTOList = TestData.returnTransactionDTOList();
        TransactionFilterDTO transactionFilterDTO = TestData.returnTransactionFilterDTOExactDate();
        int userId = 1;

        when(transactionRepository.getAll(transactionFilterDTO, userId)).thenReturn(transactionList);
        when(transactionMapper.entityToDto(transactionList)).thenReturn(transactionDTOList);

        List<TransactionDTO> actualTransactions = transactionService.getAllTransactions(transactionFilterDTO, userId);
        assertEquals(transactionDTOList, actualTransactions);
    }

    @Test
    void testGetAllByRangeDate() {
        List<Transaction> transactionList = TestData.returnTransactions();
        List<TransactionDTO> transactionDTOList = TestData.returnTransactionDTOList();
        TransactionFilterDTO transactionFilterDTO = TestData.returnTransactionFilterDTOExactDate();
        int userId = 1;

        when(transactionRepository.getAll(transactionFilterDTO, userId)).thenReturn(transactionList);
        when(transactionMapper.entityToDto(transactionList)).thenReturn(transactionDTOList);

        List<TransactionDTO> actualTransactions = transactionService.getAllTransactions(transactionFilterDTO, userId);
        assertEquals(transactionDTOList, actualTransactions);
    }

    @Test
    void testGetAll() {
        int userId = 1;
        when(transactionRepository.getAll(transactionFilterDTO, userId)).thenReturn(transactionList);
        when(transactionMapper.entityToDto(transactionList)).thenReturn(transactionDTOList);

        List<TransactionDTO> actualTransactions = transactionService.getAllTransactions(transactionFilterDTO, userId);
        assertEquals(transactionDTOList, actualTransactions);
    }

    @ParameterizedTest
    @EnumSource(ReportType.class)
    void testGetBalancedTransactionsForAllReportTypes(ReportType reportType) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now();

        when(transactionRepository.getAll(any(TransactionFilterDTO.class), anyInt()))
                .thenReturn(transactionList);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(TestData.returnUserForDonJoe()));

        List<Transaction> result = transactionService.getBalancedTransactions(startDate, endDate, reportType, 1);

        switch (reportType) {
            case ALL_TRANSACTIONS_REPORT -> assertEquals(transactionList.size(), result.size());
            case INCOMES_REPORT ->
                    assertTrue(result.stream().allMatch(t -> TransactionType.INCOME.equals(t.getType())));
            case EXPENSES_REPORT ->
                    assertTrue(result.stream().allMatch(t -> TransactionType.EXPENSE.equals(t.getType())));
        }
    }

    @Test
    void testAddIncomeTransaction() {
        TransactionDTO mockTransactionDTO = TestData.returnTransactionIncomeDTO();
        Income income = TestData.returnIncome();

        when(transactionMapper.dtoToIncomeEntity(mockTransactionDTO)).thenReturn(income);
        when(incomeRepository.save(income, income.getUserId())).thenReturn(income);

        transactionService.addTransaction(mockTransactionDTO, income.getUserId());

        verify(incomeRepository).save(income, income.getUserId());
        verify(transactionMapper).dtoToIncomeEntity(mockTransactionDTO);
    }

    @Test
    void testAddExpenseTransaction() {
        TransactionDTO mockTransactionDTO = TestData.returnNewTransactionExpenseDTOWithoutGroup();
        Expense expense = TestData.returnExpenseWithoutGroup();

        when(transactionMapper.dtoToExpenseEntity(mockTransactionDTO)).thenReturn(expense);
        when(expenseRepository.save(expense, expense.getUserId())).thenReturn(expense);

        transactionService.addTransaction(mockTransactionDTO, expense.getUserId());

        verify(expenseRepository).save(expense, expense.getUserId());
        verify(transactionMapper).dtoToExpenseEntity(mockTransactionDTO);
        verify(transactionManagementService).handleExpenseForUserOrGroup(expense, mockTransactionDTO, expense.getUserId());
    }

    @Test
    void testProcessRecurringTransactions() {
        List<Income> recurringIncomes = Collections.singletonList(TestData.returnRecurringIncomeEntity());
        List<Expense> recurringExpenses = Collections.singletonList(TestData.returnRecurringExpenseEntity());

        when(incomeRepository.getRecurringIncomeTransactions()).thenReturn(recurringIncomes);
        when(expenseRepository.getRecurringExpenseTransactions()).thenReturn(recurringExpenses);

        transactionService.processRecurringTransactions();

        verify(incomeRepository).getRecurringIncomeTransactions();
        verify(expenseRepository).getRecurringExpenseTransactions();
        verify(incomeRepository).save(recurringIncomes.get(0), recurringIncomes.get(0).getUserId());
        verify(expenseRepository).save(recurringExpenses.get(0), recurringExpenses.get(0).getUserId());
    }

    @Test
    void testAddIncomeTransactionForGroup() {
        TransactionDTO mockTransactionDTO = TestData.returnTransactionIncomeDTONONE();
        Income incomeWithGroup = TestData.returnIncomeForGroup();

        when(transactionMapper.dtoToIncomeEntity(mockTransactionDTO)).thenReturn(incomeWithGroup);
        when(incomeRepository.save(incomeWithGroup, incomeWithGroup.getUserId())).thenReturn(incomeWithGroup);

        transactionService.addTransaction(mockTransactionDTO, incomeWithGroup.getUserId());

        verify(incomeRepository).save(incomeWithGroup, incomeWithGroup.getUserId());
        verify(transactionMapper).dtoToIncomeEntity(mockTransactionDTO);
        verify(transactionManagementService).handleIncomeForUserOrGroup(eq(incomeWithGroup), eq(mockTransactionDTO), eq(incomeWithGroup.getUserId()));
    }

    @Test
    void testAddExpenseTransactionForGroup() {
        TransactionDTO mockTransactionDTO = TestData.returnTransactionExpenseDTONONE();
        Expense expenseWithGroup = TestData.returnExpenseForGroup();

        when(transactionMapper.dtoToExpenseEntity(mockTransactionDTO)).thenReturn(expenseWithGroup);
        when(expenseRepository.save(expenseWithGroup, expenseWithGroup.getUserId())).thenReturn(expenseWithGroup);

        transactionService.addTransaction(mockTransactionDTO, expenseWithGroup.getUserId());

        verify(expenseRepository).save(expenseWithGroup, expenseWithGroup.getUserId());
        verify(transactionMapper).dtoToExpenseEntity(mockTransactionDTO);
        verify(transactionManagementService).handleExpenseForUserOrGroup(eq(expenseWithGroup), eq(mockTransactionDTO), eq(expenseWithGroup.getUserId()));
    }

    @Test
    void testProcessRecurringTransactionsWithGroup() {
        Income recurringIncomeWithGroup = TestData.returnRecurringIncomeEntity();
        recurringIncomeWithGroup.setGroupId(123); // setting groupId to a non-null value

        Expense recurringExpenseWithGroup = TestData.returnRecurringExpenseEntity();
        recurringExpenseWithGroup.setGroupId(123); // setting groupId to a non-null value

        List<Income> recurringIncomesWithGroup = Collections.singletonList(recurringIncomeWithGroup);
        List<Expense> recurringExpensesWithGroup = Collections.singletonList(recurringExpenseWithGroup);

        when(incomeRepository.getRecurringIncomeTransactions()).thenReturn(recurringIncomesWithGroup);
        when(expenseRepository.getRecurringExpenseTransactions()).thenReturn(recurringExpensesWithGroup);

        transactionService.processRecurringTransactions();

        verify(incomeRepository).getRecurringIncomeTransactions();
        verify(expenseRepository).getRecurringExpenseTransactions();
        verify(incomeRepository).save(recurringIncomeWithGroup, recurringIncomeWithGroup.getUserId());
        verify(expenseRepository).save(recurringExpenseWithGroup, recurringExpenseWithGroup.getUserId());

        verify(transactionManagementService).addIncomeTransactionToGroup(recurringIncomeWithGroup.getGroupId(), recurringIncomeWithGroup);
        verify(transactionManagementService).addExpenseTransactionToGroup(recurringExpenseWithGroup.getGroupId(), recurringExpenseWithGroup);
    }
}