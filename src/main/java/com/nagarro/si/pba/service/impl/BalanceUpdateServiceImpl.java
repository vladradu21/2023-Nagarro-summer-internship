package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.InsufficientFundsException;
import com.nagarro.si.pba.model.BalanceHolder;
import com.nagarro.si.pba.service.BalanceUpdateService;
import org.springframework.stereotype.Service;

@Service
public class BalanceUpdateServiceImpl implements BalanceUpdateService {
    public void addIncomeToBalance(BalanceHolder balanceHolder, double amount) {
        double newBalance = balanceHolder.getBalance() + amount;
        balanceHolder.setBalance(newBalance);
    }
    public void subtractExpenseFromBalance(BalanceHolder balanceHolder, double amount) {
        if (balanceHolder.getBalance() < amount) {
            throw new InsufficientFundsException(ExceptionMessage.INSUFFICIENT_FUNDS.format());
        }
        double newBalance = balanceHolder.getBalance() - amount;
        balanceHolder.setBalance(newBalance);
    }
}
