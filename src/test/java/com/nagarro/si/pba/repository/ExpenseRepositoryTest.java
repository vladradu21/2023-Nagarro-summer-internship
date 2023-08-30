package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.Group;
import com.nagarro.si.pba.model.RepetitionFlow;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("inttest")
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ExpenseRepositoryTest extends AbstractMySQLContainer {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    private Expense savedExpense;
    private User savedUser;
    private Group savedGroup;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(TestData.returnUserForBobDumbledore());
        savedGroup = groupRepository.save(TestData.returnGroup());
        Expense expense = TestData.returnOneTimeExpenseEntity();
        expense.setGroupId(savedGroup.getId());
        savedExpense = expenseRepository.save(expense, savedUser.getId());
    }

    @Test
    void testFindById() {
        Optional<Expense> expenseById = expenseRepository.findById(savedExpense.getId());

        assertTrue(expenseById.isPresent());
        assertEquals(savedExpense.getId(), expenseById.get().getId());
    }

    @Test
    void testFindAll() {
        List<Expense> expenses = expenseRepository.findAll();
        assertFalse(expenses.isEmpty());
        assertEquals(1, expenses.size());
        assertEquals(savedExpense.getId(), expenses.get(0).getId());
    }

    @Test
    void testFindAllUserExpenses() {
        List<Expense> expenses = expenseRepository.findAllUserExpenses(savedUser.getId());
        assertFalse(expenses.isEmpty());
        assertEquals(1, expenses.size());
        assertEquals(savedExpense.getId(), expenses.get(0).getId());
    }

    @Test
    void testFindAllGroupExpenses() {
        List<Expense> expenses = expenseRepository.findAllGroupExpenses(savedGroup.getId());
        assertFalse(expenses.isEmpty());
        assertEquals(1, expenses.size());
        assertEquals(savedExpense.getId(), expenses.get(0).getId());
    }

    @Test
    void testGetAllGroupExpensesForSpecificUser() {
        List<Expense> expenses = expenseRepository.getAllGroupExpensesForSpecificUser(savedUser.getId(), savedGroup.getId());
        assertFalse(expenses.isEmpty());
        assertEquals(1, expenses.size());
        assertEquals(savedExpense.getId(), expenses.get(0).getId());
    }

    @Test
    void testUpdateRepetitionFlow() {
        RepetitionFlow newFlow = RepetitionFlow.ANNUALLY;
        expenseRepository.updateRepetitionFlow(savedExpense.getId(), newFlow);

        Optional<Expense> updatedIncome = expenseRepository.findById(savedExpense.getId());
        assertTrue(updatedIncome.isPresent());
        assertEquals(newFlow, updatedIncome.get().getRepetitionFlow());
    }

    @Test
    void testGetRecurringExpenseTransactions() {
        Expense expense = TestData.returnExpense();
        expense.setAddedDate(LocalDateTime.now());
        expenseRepository.save(expense, savedUser.getId());
        List<Expense> recurringTransactions = expenseRepository.getRecurringExpenseTransactions();

        assertFalse(recurringTransactions.isEmpty());

        for (Expense expenses : recurringTransactions) {
            assertEquals(RepetitionFlow.MONTHLY, expenses.getRepetitionFlow());
        }
    }
}