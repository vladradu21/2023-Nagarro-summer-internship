package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.ExpenseDTO;
import com.nagarro.si.pba.service.RecurringExpenseService;
import com.nagarro.si.pba.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/expenses")
public class RecurringExpenseController implements RecurringExpenseAPI {
    private final RecurringExpenseService recurringExpenseService;
    private final TokenService tokenService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RecurringExpenseController.class);

    public RecurringExpenseController(RecurringExpenseService recurringExpenseService, TokenService tokenService) {
        this.recurringExpenseService = recurringExpenseService;
        this.tokenService = tokenService;
    }

    @Override
    public ExpenseDTO addExpense(String token, ExpenseDTO expenseDTO) {
        LOGGER.info("Adding recurring expense for user");
        return recurringExpenseService.saveExpense(expenseDTO, tokenService.extractUserId(token));
    }
}