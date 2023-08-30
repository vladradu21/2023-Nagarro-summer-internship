package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.TransactionDTO;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.Group;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.GroupRepository;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.service.impl.TransactionManagementServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionManagementServiceTest {
    private static final double INITIAL_BALANCE = 1000.0;
    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private BalanceUpdateService balanceUpdateService;

    private TransactionManagementService transactionManagementService;

    private User user;
    private Group group;
    private Income income;
    private Expense expense;

    @BeforeEach
    void setUp() {
        transactionManagementService = new TransactionManagementServiceImpl(userRepository, groupRepository, balanceUpdateService);

        user = TestData.returnUserForDonJoe();
        user.setBalance(INITIAL_BALANCE);
        group = TestData.returnGroupId1();
        group.setBalance(INITIAL_BALANCE);
        income = TestData.returnIncome();
        expense = TestData.returnExpense();
    }

    @Test
    void testAddIncomeTransactionToUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        user.setBalance(INITIAL_BALANCE + expense.getAmount());
        doNothing().when(balanceUpdateService).addIncomeToBalance(user, income.getAmount());

        transactionManagementService.addIncomeTransactionToUser(user.getId(), income);

        verify(balanceUpdateService).addIncomeToBalance(user, income.getAmount());
        verify(userRepository).updateBalance(INITIAL_BALANCE + income.getAmount(), user.getId());
    }

    @Test
    void testAddIncomeTransactionToGroup() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        group.setBalance(INITIAL_BALANCE + income.getAmount());
        doNothing().when(balanceUpdateService).addIncomeToBalance(group, income.getAmount());

        transactionManagementService.addIncomeTransactionToGroup(group.getId(), income);

        verify(balanceUpdateService).addIncomeToBalance(group, income.getAmount());
        verify(groupRepository).updateBalance(INITIAL_BALANCE + income.getAmount(), group.getId());
    }

    @Test
    void testAddExpenseTransactionToUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        user.setBalance(INITIAL_BALANCE - expense.getAmount());
        doNothing().when(balanceUpdateService).subtractExpenseFromBalance(user, expense.getAmount());

        transactionManagementService.addExpenseTransactionToUser(user.getId(), expense);

        verify(balanceUpdateService).subtractExpenseFromBalance(user, expense.getAmount());
        verify(userRepository).updateBalance(INITIAL_BALANCE - expense.getAmount(), user.getId());
    }

    @Test
    void testAddExpenseTransactionToGroup() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        group.setBalance(INITIAL_BALANCE - expense.getAmount());
        doNothing().when(balanceUpdateService).subtractExpenseFromBalance(group, expense.getAmount());

        transactionManagementService.addExpenseTransactionToGroup(group.getId(), expense);

        verify(balanceUpdateService).subtractExpenseFromBalance(group, expense.getAmount());
        verify(groupRepository).updateBalance(INITIAL_BALANCE - expense.getAmount(), group.getId());
    }

    @Test
    void testAddIncomeTransactionToUser_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        int userId = user.getId();
        assertThrows(PbaNotFoundException.class, () ->
                transactionManagementService.addIncomeTransactionToUser(userId, income));
    }

    @Test
    void testAddIncomeTransactionToGroup_GroupNotFound() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.empty());
        int groupId = group.getId();
        assertThrows(PbaNotFoundException.class, () ->
                transactionManagementService.addIncomeTransactionToGroup(groupId, income));
    }

    @Test
    void testHandleIncomeForUser() {
        TransactionDTO transactionDTO = TestData.returnTransactionIncomeDTOWithoutGroup();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        transactionManagementService.handleIncomeForUserOrGroup(income, transactionDTO, user.getId());

        verify(userRepository).updateBalance(1000.0d, 1);
    }

    @Test
    void testHandleExpenseForUser() {
        TransactionDTO transactionDTO = TestData.returnNewTransactionExpenseDTOWithoutGroup();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        transactionManagementService.handleExpenseForUserOrGroup(expense, transactionDTO, user.getId());

        verify(userRepository).updateBalance(1000.0d, 1);
    }

    @Test
    void testHandleExpenseForGroup() {
        TransactionDTO transactionDTO = TestData.returnTransactionExpenseDTONONE();

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        transactionManagementService.handleExpenseForUserOrGroup(expense, transactionDTO, group.getId());

        verify(groupRepository).updateBalance(1000.0d, 1);
    }

    @Test
    void testHandleIncomeForGroup() {
        TransactionDTO transactionDTO = TestData.returnTransactionIncomeDTONONE();

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        transactionManagementService.handleIncomeForUserOrGroup(income, transactionDTO, group.getId());

        verify(groupRepository).updateBalance(1000.0d, 1);
    }
}