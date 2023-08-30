package com.nagarro.si.pba.repository.repositoryImpl;

import com.nagarro.si.pba.model.CategoryType;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.RepetitionFlow;
import com.nagarro.si.pba.repository.BaseRepository;
import com.nagarro.si.pba.repository.IncomeRepository;
import com.nagarro.si.pba.utils.TransactionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class IncomeRepositoryImpl extends BaseRepository implements IncomeRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(IncomeRepositoryImpl.class);

    @Autowired
    public IncomeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Income> findById(int id) {
        LOGGER.info("Finding income by ID: {}", id);
        return handleEmptyResult(() -> Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM income WHERE id = ?", new BeanPropertyRowMapper<>(Income.class), id)));
    }

    public List<Income> findAll() {
        LOGGER.info("Fetching all incomes");
        String query = "select * from income";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Income.class));
    }

    public List<Income> findAllRecurringIncome() {
        String query = "select * from income where repetition_flow <> 'NONE'";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Income.class));
    }


    public List<Income> findAllUserIncomes(int id) {
        LOGGER.info("Fetching all incomes for user ID: {}", id);
        String query = "SELECT * FROM income WHERE userId = ?";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Income.class), id);
    }

    public List<Income> findAllGroupIncomes(int id) {
        LOGGER.info("Fetching all group incomes for group ID: {}", id);
        String query = "SELECT * FROM income WHERE group_id = ?";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Income.class), id);
    }

    public List<Income> getAllGroupIncomesForSpecificUser(int userId, int groupId) {
        LOGGER.info("Fetching all group incomes for user ID: {} and group ID: {}", userId, groupId);
        String query = "SELECT * FROM income WHERE userId = ? AND group_id = ?";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Income.class), userId, groupId);
    }

    public Income save(Income income, int userId) {
        LOGGER.info("Saving new income for user ID: {}", userId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO income (type, amount, category, name, currency, description, addedDate, repetition_flow, userId, group_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, String.valueOf(CategoryType.INCOME));
            ps.setDouble(2, income.getAmount());
            ps.setString(3, income.getCategory());
            ps.setString(4, income.getName());
            ps.setString(5, income.getCurrency().name());
            ps.setString(6, income.getDescription());
            ps.setObject(7, income.getAddedDate());
            ps.setString(8, income.getRepetitionFlow().name());
            ps.setInt(9, userId);
            if (income.getGroupId() == null) {
                ps.setNull(10, Types.INTEGER);
            } else {
                ps.setInt(10, income.getGroupId());
            }
            return ps;
        }, keyHolder);

        int generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        income.setId(generatedId);

        LOGGER.info("Saved new income with ID: {}", generatedId);
        return income;

    }

    public void updateRepetitionFlow(int id, RepetitionFlow repetitionFlow) {
        String sql = "UPDATE income SET repetition_flow = ? WHERE id = ?";
        jdbcTemplate.update(sql, repetitionFlow.toString(), id);
    }

    public List<Income> getRecurringIncomeTransactions() {
        List<Income> incomes = findAllRecurringIncome();
        List<Income> filteredIncomes = new ArrayList<>();

        for (Income income : incomes) {
            if (TransactionUtils.checkTransactionDate(income)) {
                updateRepetitionFlow(income.getId(), RepetitionFlow.NONE);
                income.setAddedDate(TransactionUtils.getNextDate(income.getAddedDate(), income.getRepetitionFlow()));
                filteredIncomes.add(income);
            }
        }
        return filteredIncomes;
    }
}