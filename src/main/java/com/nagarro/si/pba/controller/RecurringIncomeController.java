package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.IncomeDTO;
import com.nagarro.si.pba.service.RecurringIncomeService;
import com.nagarro.si.pba.service.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/incomes")
public class RecurringIncomeController implements RecurringIncomeApi {
    private final RecurringIncomeService recurringIncomeService;
    private final TokenService tokenService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RecurringExpenseController.class);

    public RecurringIncomeController(RecurringIncomeService recurringIncomeService, TokenService tokenService) {
        this.recurringIncomeService = recurringIncomeService;
        this.tokenService = tokenService;
    }

    @Override
    public IncomeDTO addIncome(String token, IncomeDTO incomeDTO) {
        LOGGER.info("Adding recurring income for user");
        return recurringIncomeService.saveIncome(incomeDTO, tokenService.extractUserId(token));
    }
}