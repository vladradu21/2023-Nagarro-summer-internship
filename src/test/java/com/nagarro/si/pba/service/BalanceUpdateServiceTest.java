package com.nagarro.si.pba.service;

import com.nagarro.si.pba.exceptions.InsufficientFundsException;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.service.impl.BalanceUpdateServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BalanceUpdateServiceTest {

    private static final double INITIAL_USER_BALANCE = 1000.0;
    private static final double AMOUNT_TO_ADD = 500.0;
    private static final double AMOUNT_TO_SUBTRACT = 200.0;
    private static final double INSUFFICIENT_FUNDS_BALANCE = 100.0;

    private BalanceUpdateService balanceUpdateService;
    private User user;

    @BeforeEach
    void setup() {
        balanceUpdateService = new BalanceUpdateServiceImpl();
        user = TestData.returnUserForDonJoe();
        user.setBalance(INITIAL_USER_BALANCE);
    }

    @Test
    void testAddIncomeToBalance() {
        balanceUpdateService.addIncomeToBalance(user, AMOUNT_TO_ADD);
        assertEquals(INITIAL_USER_BALANCE + AMOUNT_TO_ADD, user.getBalance());
    }

    @Test
    void testSubtractExpenseFromBalanceWithSufficientFunds() {
        balanceUpdateService.subtractExpenseFromBalance(user, AMOUNT_TO_SUBTRACT);
        assertEquals(INITIAL_USER_BALANCE - AMOUNT_TO_SUBTRACT, user.getBalance());
    }

    @Test
    void testSubtractExpenseFromBalanceWithInsufficientFunds() {
        User insufficientFundsUser = new User();
        insufficientFundsUser.setBalance(INSUFFICIENT_FUNDS_BALANCE);

        assertThrows(InsufficientFundsException.class, () -> balanceUpdateService.subtractExpenseFromBalance(insufficientFundsUser, AMOUNT_TO_SUBTRACT));
    }
}
