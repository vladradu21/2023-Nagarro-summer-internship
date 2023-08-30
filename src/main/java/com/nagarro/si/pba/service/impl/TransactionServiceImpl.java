package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.dto.TransactionDTO;
import com.nagarro.si.pba.dto.TransactionFilterDTO;
import com.nagarro.si.pba.mapper.TransactionMapper;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.ReportType;
import com.nagarro.si.pba.model.Transaction;
import com.nagarro.si.pba.model.TransactionType;
import com.nagarro.si.pba.repository.ExpenseRepository;
import com.nagarro.si.pba.repository.IncomeRepository;
import com.nagarro.si.pba.repository.TransactionRepository;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.service.TransactionManagementService;
import com.nagarro.si.pba.service.TransactionService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@EnableScheduling
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionManagementService transactionManagementService;
    private final UserRepository userRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper, UserRepository userRepository, IncomeRepository incomeRepository, ExpenseRepository expenseRepository, TransactionManagementService transactionManagementService) {
        this.transactionRepository = transactionRepository;
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.transactionMapper = transactionMapper;
        this.transactionManagementService = transactionManagementService;
        this.userRepository = userRepository;
    }

    @Override
    public List<TransactionDTO> getAllTransactions(TransactionFilterDTO transactionFilterDTO, int userId) {
        return transactionMapper.entityToDto(transactionRepository.getAll(transactionFilterDTO, userId));
    }


    @Override
    public void addTransaction(TransactionDTO transactionDTO, int userId) {
        if (TransactionType.INCOME.equals(transactionDTO.type())) {
            Income toSaveIncome = transactionMapper.dtoToIncomeEntity(transactionDTO);
            incomeRepository.save(toSaveIncome, userId);
            transactionManagementService.handleIncomeForUserOrGroup(toSaveIncome, transactionDTO, userId);
        } else if (TransactionType.EXPENSE.equals(transactionDTO.type())) {
            Expense toSaveExpense = transactionMapper.dtoToExpenseEntity(transactionDTO);
            expenseRepository.save(toSaveExpense, userId);
            transactionManagementService.handleExpenseForUserOrGroup(toSaveExpense, transactionDTO, userId);
        }
    }
    @Scheduled(cron = "0 * * * * ?")
    public void processRecurringTransactions() {
        List<Income> recurringIncomes = incomeRepository.getRecurringIncomeTransactions();
        for (Income income : recurringIncomes) {
            incomeRepository.save(income, income.getUserId());
            if(income.getGroupId()!= null) {
                transactionManagementService.addIncomeTransactionToGroup(income.getGroupId(), income);
            } else {
                transactionManagementService.addIncomeTransactionToUser(income.getUserId(), income);
            }
        }

        List<Expense> recurringExpenses = expenseRepository.getRecurringExpenseTransactions();
        for (Expense expense : recurringExpenses) {
            expenseRepository.save(expense, expense.getUserId());
            if(expense.getGroupId()!= null) {
                transactionManagementService.addExpenseTransactionToGroup(expense.getGroupId(), expense);
            } else {
                transactionManagementService.addExpenseTransactionToUser(expense.getUserId(), expense);
            }
        }
    }

    public List<Transaction> getBalancedTransactions(LocalDateTime startDate, LocalDateTime endDate, ReportType reportType, int userId) {
        List<Transaction> transactions = transactionRepository.getAll(new TransactionFilterDTO(null, startDate, endDate), userId);
        double currentBalance = calculateInitialBalance(userId, transactions);

        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction transaction = transactions.get(i);
            if (TransactionType.INCOME.equals(transaction.getType())) {
                currentBalance += transaction.getAmount();
            } else if (TransactionType.EXPENSE.equals(transaction.getType())) {
                currentBalance -= transaction.getAmount();
            }
            transaction.setBalanceAfterTransaction(currentBalance);
        }


        return switch (reportType) {
            case ALL_TRANSACTIONS_REPORT -> transactions;
            case INCOMES_REPORT -> transactions.stream()
                    .filter(t -> TransactionType.INCOME.equals(t.getType()))
                    .toList();
            case EXPENSES_REPORT -> transactions.stream()
                    .filter(t -> TransactionType.EXPENSE.equals(t.getType()))
                    .toList();
        };

    }

    private double calculateInitialBalance(int userId, List<Transaction> transactions) {

        double initialBalance = userRepository.findById(userId).get().getBalance();

        for (Transaction transaction : transactions) {
            if (TransactionType.INCOME.equals(transaction.getType())) {
                initialBalance -= transaction.getAmount();
            } else if (TransactionType.EXPENSE.equals(transaction.getType())) {
                initialBalance += transaction.getAmount();
            }
        }
        return initialBalance;
    }
}