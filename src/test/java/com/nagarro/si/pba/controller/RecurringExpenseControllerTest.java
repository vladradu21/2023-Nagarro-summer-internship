package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.ExpenseDTO;
import com.nagarro.si.pba.service.RecurringExpenseService;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.utils.TestData;
import com.nagarro.si.pba.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class RecurringExpenseControllerTest extends BaseControllerTest<RecurringExpenseController> {
    @Mock
    private RecurringExpenseService recurringExpenseService;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private RecurringExpenseController recurringExpenseController;

    @Override
    protected RecurringExpenseController getController() {
        return recurringExpenseController;
    }

    @Test
    void testAddNewExpenseDTO() throws Exception {
        ExpenseDTO expenseDTO = TestData.returnRecurringExpenseDTO();
        int userId = 1;
        String tokenString = "sampleToken";

        when(tokenService.extractUserId(tokenString)).thenReturn(userId);
        when(recurringExpenseService.saveExpense(expenseDTO, userId)).thenReturn(expenseDTO);

        mockMvc.perform(post("/expenses/recurring")
                        .requestAttr("jwtToken", tokenString)
                        .content(TestUtils.asJsonString(expenseDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(tokenService).extractUserId(tokenString);
        verify(recurringExpenseService).saveExpense(expenseDTO, userId);
    }
}