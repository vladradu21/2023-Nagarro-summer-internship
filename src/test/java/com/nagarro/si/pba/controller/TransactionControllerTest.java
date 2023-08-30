package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.TransactionDTO;
import com.nagarro.si.pba.dto.TransactionFilterDTO;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.TransactionService;
import com.nagarro.si.pba.utils.TestData;
import com.nagarro.si.pba.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {
    private final int mockUserId = 1;
    private final String mockToken = "mockToken";
    private MockMvc mockMvc;
    @Mock
    private TransactionService transactionService;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void testGetAllTransactionsByExactDate() throws Exception {
        TransactionFilterDTO transactionFilterDTO = TestData.returnTransactionFilterDTOExactDate();
        List<TransactionDTO> transactionDTOList = TestData.returnTransactionDTOListWithOnetimeIncome();


        when(tokenService.extractUserId(mockToken)).thenReturn(mockUserId);
        when(transactionService.getAllTransactions(transactionFilterDTO, mockUserId)).thenReturn(transactionDTOList);

        mockMvc.perform(post("/transactions")
                        .header("Authorization", "Bearer " + mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(transactionFilterDTO))
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtils.asJsonString(transactionDTOList)));

        verify(transactionService).getAllTransactions(transactionFilterDTO, mockUserId);
    }

    @Test
    void testGetAllTransactionsByDateRange() throws Exception {
        TransactionFilterDTO transactionFilterDTO = TestData.returnTransactionFilterDTORangeDates();
        List<TransactionDTO> transactionDTOList = TestData.returnTransactionDTOListWithOnetimeIncome();

        when(tokenService.extractUserId(mockToken)).thenReturn(mockUserId);
        when(transactionService.getAllTransactions(transactionFilterDTO, mockUserId)).thenReturn(TestData.returnTransactionDTOListWithOnetimeIncome());

        mockMvc.perform(post("/transactions")
                        .header("Authorization", "Bearer " + mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(transactionFilterDTO))
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtils.asJsonString(transactionDTOList)));

        verify(transactionService).getAllTransactions(transactionFilterDTO, mockUserId);
    }

    @Test
    void testGetAllTransactionsNoFilter() throws Exception {
        TransactionFilterDTO transactionFilterDTO = TestData.returnTransactionFilterDTOEmpty();

        when(tokenService.extractUserId(mockToken)).thenReturn(mockUserId);
        when(transactionService.getAllTransactions(transactionFilterDTO, mockUserId)).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/transactions")
                        .header("Authorization", "Bearer " + mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(transactionFilterDTO))
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk())
                .andExpect(content().json(Collections.emptyList().toString()));

        verify(transactionService).getAllTransactions(transactionFilterDTO, mockUserId);
    }


    @Test
    void testAddIncomeTransaction() throws Exception {
        TransactionDTO mockIncomeTransactionDTO = TestData.returnTransactionIncomeDTO();
        when(tokenService.extractUserId(mockToken)).thenReturn(mockUserId);

        mockMvc.perform(post("/transactions/add")
                        .header("Authorization", "Bearer " + mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(mockIncomeTransactionDTO))
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk());

        verify(transactionService).addTransaction(mockIncomeTransactionDTO, mockUserId);
    }

    @Test
    void testAddExpenseTransaction() throws Exception {
        TransactionDTO mockExpenseTransactionDTO = TestData.returnTransactionExpenseDTO();
        when(tokenService.extractUserId(mockToken)).thenReturn(mockUserId);

        mockMvc.perform(post("/transactions/add")
                        .header("Authorization", "Bearer " + mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(mockExpenseTransactionDTO))
                        .requestAttr("jwtToken", mockToken))
                .andExpect(status().isOk());

        verify(transactionService).addTransaction(mockExpenseTransactionDTO, mockUserId);
    }
}