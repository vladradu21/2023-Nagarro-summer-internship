package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.dto.TransactionDTO;
import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.Group;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.RepetitionFlow;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.GroupRepository;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.service.BalanceUpdateService;
import com.nagarro.si.pba.service.TransactionManagementService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionManagementServiceImpl implements TransactionManagementService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final BalanceUpdateService balanceUpdateService;

    public TransactionManagementServiceImpl(UserRepository userRepository, GroupRepository groupRepository, BalanceUpdateService balanceUpdateService) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.balanceUpdateService = balanceUpdateService;

    }

    @Override
    public void addIncomeTransactionToUser(int userId, Income income) {
        User user = getUserByIdForTransaction(userId);
        balanceUpdateService.addIncomeToBalance(user, income.getAmount());
        userRepository.updateBalance(user.getBalance(), userId);
    }

    @Override
    public void addIncomeTransactionToGroup(int groupId, Income income) {
        Group group = getGroupByIdForTransaction(groupId);
        balanceUpdateService.addIncomeToBalance(group, income.getAmount());
        groupRepository.updateBalance(group.getBalance(), groupId);
    }

    @Override
    public void addExpenseTransactionToUser(int userId, Expense expense) {
        User user = getUserByIdForTransaction(userId);
        balanceUpdateService.subtractExpenseFromBalance(user, expense.getAmount());
        userRepository.updateBalance(user.getBalance(), user.getId());
    }

    @Override
    public void addExpenseTransactionToGroup(int groupId, Expense expense) {
        Group group = getGroupByIdForTransaction(groupId);
        balanceUpdateService.subtractExpenseFromBalance(group, expense.getAmount());
        groupRepository.updateBalance(group.getBalance(), group.getId());
    }

    @Override
    public void handleIncomeForUserOrGroup(Income income, TransactionDTO transactionDTO, int userId) {
        if (RepetitionFlow.NONE.equals(transactionDTO.repetitionFlow())) {
            if (transactionDTO.groupId() != null) {
                addIncomeTransactionToGroup(transactionDTO.groupId(), income);
            } else {
                addIncomeTransactionToUser(userId, income);
            }
        }
    }

    @Override
    public void handleExpenseForUserOrGroup(Expense expense, TransactionDTO transactionDTO, int userId) {
        if (RepetitionFlow.NONE.equals(transactionDTO.repetitionFlow())) {
            if (transactionDTO.groupId() != null) {
                addExpenseTransactionToGroup(transactionDTO.groupId(), expense);
            } else {
                addExpenseTransactionToUser(userId, expense);
            }
        }
    }

    private User getUserByIdForTransaction(int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else throw new PbaNotFoundException(ExceptionMessage.USERNAME_NOT_FOUND.format(userId));
    }

    private Group getGroupByIdForTransaction(int groupId) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isPresent()) {
            return optionalGroup.get();
        } else throw new PbaNotFoundException(ExceptionMessage.GROUP_NOT_FOUND.format(groupId));
    }
}
