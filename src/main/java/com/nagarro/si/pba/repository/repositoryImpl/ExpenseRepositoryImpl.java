package com.nagarro.si.pba.repository.repositoryImpl;

import com.nagarro.si.pba.model.CategoryType;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.RepetitionFlow;
import com.nagarro.si.pba.repository.BaseRepository;
import com.nagarro.si.pba.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nagarro.si.pba.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ExpenseRepositoryImpl extends BaseRepository implements ExpenseRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseRepositoryImpl.class);

    @Autowired
    public ExpenseRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Expense> findById(int id) {
        LOGGER.info("Finding expense by ID: {}", id);
        return handleEmptyResult(() -> Optional.ofNullable(jdbcTemplate.queryForObject("select * from expense where id = ?", new BeanPropertyRowMapper<>(Expense.class), id)));
    }

    public List<Expense> findAll() {
        LOGGER.info("Fetching all expenses");
        String query = "select * from expense";

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Expense.class));
    }

    public List<Expense> findAllUserExpenses(int id) {
        LOGGER.info("Fetching all expenses for user ID: {}", id);
        String query = "select * from expense where userId = ?";

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Expense.class), id);
    }

    public List<Expense> findAllGroupExpenses(int id) {
        LOGGER.info("Fetching all group expenses for group ID: {}", id);
        String query = "select * from expense where group_id = ?";

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Expense.class), id);
    }

    public List<Expense> getAllGroupExpensesForSpecificUser(int userId, int groupId) {
        LOGGER.info("Fetching all group expenses for user ID: {} and group ID: {}", userId, groupId);
        String query = "select * from expense where userId = ? and group_id = ?";

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Expense.class), userId, groupId);
    }

    @Override
    public Expense save(Expense expense, int userId) {
        LOGGER.info("Saving new expense for user ID: {}", userId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "insert into expense (type, amount, category, name, currency, description, addedDate, repetition_flow, userId, group_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement queryRunner = connection.prepareStatement(query, new String[]{"id"});
            queryRunner.setString(1, String.valueOf(CategoryType.EXPENSE));
            queryRunner.setDouble(2, expense.getAmount());
            queryRunner.setString(3, String.valueOf(expense.getCategory()));
            queryRunner.setString(4, expense.getName());
            queryRunner.setString(5, String.valueOf(expense.getCurrency()));
            queryRunner.setString(6, expense.getDescription());
            queryRunner.setObject(7, expense.getAddedDate());
            queryRunner.setString(8, expense.getRepetitionFlow().name());
            queryRunner.setInt(9, userId);
            if (expense.getGroupId() == null) {
                queryRunner.setNull(10, Types.INTEGER);
            } else {
                queryRunner.setInt(10, expense.getGroupId());
            }
            return queryRunner;
        }, keyHolder);

        int generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        expense.setId(generatedId);

        LOGGER.info("Saved new expense with ID: {}", generatedId);
        return expense;
    }

    public void updateRepetitionFlow(int id, RepetitionFlow repetitionFlow) {
        String sql = "UPDATE expense SET repetition_flow = ? WHERE id = ?";
        jdbcTemplate.update(sql, repetitionFlow.toString(), id);
    }

    public List<Expense> getRecurringExpenseTransactions() {
        List<Expense> expenses = findAllRecurringExpenses();
        List<Expense> filteredExpenses = new ArrayList<>();

        for (Expense expense : expenses) {
            if (TransactionUtils.checkTransactionDate(expense)) {
                updateRepetitionFlow(expense.getId(), RepetitionFlow.NONE);
                expense.setAddedDate(TransactionUtils.getNextDate(expense.getAddedDate(), expense.getRepetitionFlow()));
                filteredExpenses.add(expense);
            }
        }
        return filteredExpenses;
    }

    public List<Expense> findAllRecurringExpenses() {
        String query = "select * from expense where repetition_flow <> 'NONE'";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Expense.class));
    }
}