package com.nagarro.si.pba.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nagarro.si.pba.dto.TransactionDTO;
import com.nagarro.si.pba.dto.TransactionFilterDTO;
import com.nagarro.si.pba.mapper.TransactionMapper;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.Transaction;
import com.nagarro.si.pba.model.TransactionType;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.AbstractMySQLContainer;
import com.nagarro.si.pba.repository.ExpenseRepository;
import com.nagarro.si.pba.repository.IncomeRepository;
import com.nagarro.si.pba.repository.TransactionRepository;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.utils.TestData;
import com.nagarro.si.pba.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("inttest")
@AutoConfigureMockMvc(addFilters = false)
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TransactionControllerIntegrationTest extends AbstractMySQLContainer {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionMapper transactionMapper;

    @Autowired
    JWTGenerator jwtGenerator;

    private final User user= TestData.returnUserForDonJoe();
    private final Income income = TestData.returnIncomeNONE();
    private final Expense expense = TestData.returnExpenseNONE();

    @BeforeEach
    void setup() {
        userRepository.save(user);
        incomeRepository.save(income, user.getId());
        expenseRepository.save(expense, user.getId());
    }

    @Test
    void testGetAllTransactionsWitNoFilter() throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(income);
        transactions.add(expense);
        List<TransactionDTO> transactionDTOS = transactionMapper.entityToDto(transactions);
        String jwt = jwtGenerator.generateJwtForLogin(user.getId(), user.getUsername());

        String responseContent =  mockMvc.perform(post("/transactions")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(TestData.returnTransactionFilterDTOEmpty()))
                        .requestAttr("jwtToken", jwt))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse().getContentAsString();

        List<TransactionDTO> actualTransactionDtoList = TestUtils.OBJECT_MAPPER.readValue(responseContent, new TypeReference<>() {});

        assertEquals(transactionDTOS, actualTransactionDtoList);
    }

    @Test
    void testGetAllTransactionsByExactDate() throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(income);
        List<TransactionDTO> transactionDTOS = transactionMapper.entityToDto(transactions);
        String jwt = jwtGenerator.generateJwtForLogin(user.getId(), user.getUsername());

        String responseContent =  mockMvc.perform(post("/transactions")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(TestData.returnTransactionFilterDTOExactDate()))
                        .requestAttr("jwtToken", jwt))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse().getContentAsString();

        List<TransactionDTO> actualTransactionDtoList = TestUtils.OBJECT_MAPPER.readValue(responseContent, new TypeReference<>() {});

        assertEquals(transactionDTOS, actualTransactionDtoList);
    }

    @Test
    void testGetAllTransactionsByRangeDate() throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(income);
        List<TransactionDTO> transactionDTOS = transactionMapper.entityToDto(transactions);
        String jwt = jwtGenerator.generateJwtForLogin(user.getId(), user.getUsername());

        String responseContent =  mockMvc.perform(post("/transactions")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(TestData.returnTransactionFilterDTORangeDates()))
                        .requestAttr("jwtToken", jwt))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse().getContentAsString();

        List<TransactionDTO> actualTransactionDtoList = TestUtils.OBJECT_MAPPER.readValue(responseContent, new TypeReference<>() {});

        assertEquals(transactionDTOS, actualTransactionDtoList);
    }

    @Test
    void testAddTransaction() throws Exception {
        TransactionDTO newTransactionDTO = TestData.returnTransactionIncomeDTOWithoutGroup();
        String jwt = jwtGenerator.generateJwtForLogin(user.getId(), user.getUsername());

        mockMvc.perform(post("/transactions/add")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(newTransactionDTO))
                        .requestAttr("jwtToken", jwt))
                .andExpect(status().isOk());

        TransactionFilterDTO filter = new TransactionFilterDTO(null, LocalDateTime.now().minusYears(10), LocalDateTime.now().plusYears(10));
        List<Transaction> savedTransactions = transactionRepository.getAll(filter, user.getId());
        assertEquals(3, savedTransactions.size());

        Class<? extends Transaction> expectedClass = (newTransactionDTO.type() == TransactionType.INCOME) ? Income.class : Expense.class;

        Transaction savedTransaction = savedTransactions.stream()
                .filter(t -> t.getClass().equals(expectedClass))
                .findFirst()
                .orElseThrow();

        assertEquals(newTransactionDTO.amount(), savedTransaction.getAmount());
        assertEquals(newTransactionDTO.type(), savedTransaction.getType());
    }
}