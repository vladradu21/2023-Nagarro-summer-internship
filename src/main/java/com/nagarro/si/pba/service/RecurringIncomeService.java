package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.IncomeDTO;

public interface RecurringIncomeService {
    IncomeDTO saveIncome(IncomeDTO incomeDTO, int userId);
}