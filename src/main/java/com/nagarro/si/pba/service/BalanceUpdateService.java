package com.nagarro.si.pba.service;

import com.nagarro.si.pba.model.BalanceHolder;

public interface BalanceUpdateService {
    void addIncomeToBalance(BalanceHolder balanceHolder, double amount);
    void subtractExpenseFromBalance(BalanceHolder balanceHolder, double amount);
}
