package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.dto.TransactionFilterDTO;
import com.nagarro.si.pba.exceptions.InvalidInputException;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.Transaction;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("inttest")
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TransactionRepositoryTest extends AbstractMySQLContainer {
    private final Income income = TestData.returnOneTimeIncomeEntity();
    private final Expense expense = TestData.returnOneTimeExpenseEntity();
    private final User user = TestData.returnUserForDonJoe();
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.save(user);
        incomeRepository.save(income, user.getId());
        expenseRepository.save(expense, user.getId());
    }

    @Test
    void testGetAllNoFilter() {
        TransactionFilterDTO transactionFilterDTO = TestData.returnTransactionFilterDTOEmpty();
        List<Transaction> transactionList = transactionRepository.getAll(transactionFilterDTO, user.getId());

        Transaction firstTransaction = transactionList.get(0);
        Transaction secondTransaction = transactionList.get(1);

        assertEquals(2, transactionList.size());
        assertTrue(firstTransaction instanceof Income);
        assertTrue(secondTransaction instanceof Expense);
        assertEquals(income.getId(), firstTransaction.getId());
        assertEquals(expense.getId(), secondTransaction.getId());
    }

    @Test
    void testGetAllExactDate() {
        TransactionFilterDTO transactionFilterDTO = TestData.returnTransactionFilterDTOExactDate();
        List<Transaction> transactionList = transactionRepository.getAll(transactionFilterDTO, user.getId());

        Transaction transaction = transactionList.get(0);

        assertEquals(1, transactionList.size());
        assertTrue(transaction instanceof Income);
        assertEquals(income.getId(), transaction.getId());
    }


    @Test
    void testGetAllDateRange() {
        TransactionFilterDTO transactionFilterDTO = TestData.returnTransactionFilterDTORangeDates();
        List<Transaction> transactionList = transactionRepository.getAll(transactionFilterDTO, user.getId());

        Transaction transaction = transactionList.get(0);

        assertEquals(1, transactionList.size());
        assertTrue(transaction instanceof Income);
        assertEquals(income.getId(), transaction.getId());
    }

    @Test
    void testInvalidFilter() {
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        LocalDateTime exactDate = LocalDateTime.of(2023, 6, 15, 12, 0);

        assertThrows(InvalidInputException.class, () -> new TransactionFilterDTO(exactDate, startDate, endDate));
    }
}