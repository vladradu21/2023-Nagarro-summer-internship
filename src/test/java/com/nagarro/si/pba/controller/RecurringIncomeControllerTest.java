package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.IncomeDTO;
import com.nagarro.si.pba.service.RecurringIncomeService;
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
class RecurringIncomeControllerTest extends BaseControllerTest<RecurringIncomeController> {
    @Mock
    private RecurringIncomeService recurringIncomeService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private RecurringIncomeController recurringIncomeController;

    @Override
    protected RecurringIncomeController getController() {
        return recurringIncomeController;
    }

    @Test
    void create_IncomeDTOGiven() throws Exception {
        IncomeDTO incomeDto = TestData.returnRecurringIncomeDTO();
        int userId = 1;
        String tokenString = "sampleToken";

        when(tokenService.extractUserId(tokenString)).thenReturn(userId);
        when(recurringIncomeService.saveIncome(incomeDto, userId)).thenReturn(incomeDto);

        mockMvc.perform(post("/incomes/recurring")
                        .requestAttr("jwtToken", tokenString)
                        .content(TestUtils.asJsonString(incomeDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(tokenService).extractUserId(tokenString);
        verify(recurringIncomeService).saveIncome(incomeDto, userId);
    }
}