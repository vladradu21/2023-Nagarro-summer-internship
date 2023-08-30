package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.dto.ExpenseDTO;
import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.PbaConflictException;
import com.nagarro.si.pba.mapper.ExpenseMapper;
import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.RepetitionFlow;
import com.nagarro.si.pba.repository.ExpenseRepository;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.service.BnrCurrencyService;
import com.nagarro.si.pba.service.RecurringExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RecurringExpenseServiceImpl implements RecurringExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final BnrCurrencyService bnrCurrencyService;
    private final UserRepository userRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(RecurringExpenseServiceImpl.class);

    public RecurringExpenseServiceImpl(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper,
            BnrCurrencyService bnrCurrencyService, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.bnrCurrencyService = bnrCurrencyService;
        this.userRepository = userRepository;
    }

    public ExpenseDTO saveExpense(ExpenseDTO expenseDTO, int userId) {
        LOGGER.info("Saving recurring expense for user with ID: {}", userId);
        if (expenseDTO.repetitionFlow() == RepetitionFlow.NONE) {
            LOGGER.warn("Invalid recurring expense with repetition flow NONE for user with ID: {}", userId);
            throw new PbaConflictException(ExceptionMessage.INVALID_RECURRING_EXPENSE.format());
        }

        Expense expense = expenseMapper.dtoToEntity(expenseDTO);

        Currency defaultCurrencyOfUser = userRepository.getDefaultCurrency(userId);
        expense.setAmount(
                bnrCurrencyService.convert(expense.getAmount(), expense.getCurrency(), defaultCurrencyOfUser));
        expense.setCurrency(defaultCurrencyOfUser);

        Expense savedExpense = expenseRepository.save(expense, userId);

        LOGGER.info("Recurring expense saved successfully for user with ID: {}", userId);
        return expenseMapper.entityToDTO(savedExpense);
    }
}