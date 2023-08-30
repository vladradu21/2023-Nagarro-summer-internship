package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.dto.IncomeDTO;
import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.PbaConflictException;
import com.nagarro.si.pba.mapper.IncomeMapper;
import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.RepetitionFlow;
import com.nagarro.si.pba.repository.IncomeRepository;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.service.BnrCurrencyService;
import com.nagarro.si.pba.service.RecurringIncomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class RecurringIncomeServiceImpl implements RecurringIncomeService {
    private final IncomeRepository incomeRepository;
    private final IncomeMapper incomeMapper;
    private final BnrCurrencyService bnrCurrencyService;
    private final UserRepository userRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(RecurringIncomeServiceImpl.class);

    public RecurringIncomeServiceImpl(IncomeRepository incomeRepository, IncomeMapper incomeMapper,
            BnrCurrencyService bnrCurrencyService, UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.incomeMapper = incomeMapper;
        this.bnrCurrencyService = bnrCurrencyService;
        this.userRepository = userRepository;
    }

    public IncomeDTO saveIncome(IncomeDTO incomeDTO, int userId) {
        LOGGER.info("Saving recurring income for user with ID: {}", userId);
        if (RepetitionFlow.NONE.equals(incomeDTO.repetitionFlow())) {
            LOGGER.warn("Invalid recurring income with repetition flow NONE for user with ID: {}", userId);
            throw new PbaConflictException(ExceptionMessage.INVALID_RECURRING_INCOME.format());
        }

        Income income = incomeMapper.dtoToEntity(incomeDTO);

        Currency defaultCurrencyOfUser = userRepository.getDefaultCurrency(userId);
        income.setAmount(bnrCurrencyService.convert(income.getAmount(), income.getCurrency(), defaultCurrencyOfUser));
        income.setCurrency(defaultCurrencyOfUser);

        Income savedIncome = incomeRepository.save(income, userId);


        LOGGER.info("Recurring income saved successfully for user with ID: {}", userId);
        return incomeMapper.entityToDTO(savedIncome);
    }
}