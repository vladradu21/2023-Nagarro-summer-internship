package com.nagarro.si.pba.repository.repositoryImpl;

import com.nagarro.si.pba.dto.TransactionFilterDTO;
import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.RepetitionFlow;
import com.nagarro.si.pba.model.Transaction;
import com.nagarro.si.pba.model.TransactionType;
import com.nagarro.si.pba.repository.TransactionRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public TransactionRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Transaction> getAll(TransactionFilterDTO transactionFilterDTO, int userId) {
        LocalDateTime exactDate = transactionFilterDTO.exactDate();
        LocalDateTime startDate = transactionFilterDTO.startDate();
        LocalDateTime endDate = transactionFilterDTO.endDate();

        String sql = """
                SELECT id, type, category, name, amount, currency, description, addedDate, repetition_flow, userId, group_id\s
                FROM income\s
                WHERE\s
                    (:startDate IS NULL OR addedDate >= :startDate)\s
                    AND (:endDate IS NULL OR addedDate <= :endDate)\s
                    AND (:exactDate IS NULL OR addedDate = :exactDate)\s
                    AND userId = :userId\s
                    AND repetition_flow = 'NONE'
                UNION\s
                SELECT id, type, category, name, amount, currency, description, addedDate, repetition_flow, userId, group_id\s
                FROM expense\s
                WHERE\s
                    (:startDate IS NULL OR addedDate >= :startDate)\s
                    AND (:endDate IS NULL OR addedDate <= :endDate)\s
                    AND (:exactDate IS NULL OR addedDate = :exactDate)\s
                    AND userId = :userId\s
                    AND repetition_flow = 'NONE'
                ORDER BY addedDate DESC;
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("exactDate", exactDate);
        params.addValue("startDate", startDate);
        params.addValue("endDate", endDate);
        params.addValue("userId", userId);

        return namedParameterJdbcTemplate.query(sql, params, this::mapRowToTransaction);
    }

    private List<Transaction> mapRowToTransaction(ResultSet resultSet) throws SQLException {
        List<Transaction> transactionList = new ArrayList<>();

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            TransactionType type = TransactionType.valueOf(resultSet.getString("type"));
            double amount = resultSet.getDouble("amount");
            String category = resultSet.getString("category");
            String name = resultSet.getString("name");
            Currency currency = Currency.valueOf(resultSet.getString("currency"));
            String description = resultSet.getString("description");
            LocalDateTime addedDate = resultSet.getTimestamp("addedDate").toLocalDateTime();
            RepetitionFlow repetitionFlow = RepetitionFlow.valueOf(resultSet.getString("repetition_flow"));
            int userId = resultSet.getInt("userId");
            Integer groupId = (Integer) resultSet.getObject("group_id");

            if (type.equals(TransactionType.INCOME)) {
                transactionList.add(new Income(
                        id,
                        category,
                        name,
                        amount,
                        description,
                        currency,
                        addedDate,
                        repetitionFlow,
                        userId,
                        groupId)
                );
            } else {
                transactionList.add(new Expense(
                        id,
                        category,
                        name,
                        amount,
                        description,
                        currency,
                        addedDate,
                        repetitionFlow,
                        userId,
                        groupId)
                );
            }
        }
        return transactionList;
    }
}