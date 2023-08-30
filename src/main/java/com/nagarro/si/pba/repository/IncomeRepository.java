package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.dto.ReportRequestDTO;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.RepetitionFlow;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface IncomeRepository {
    Optional<Income> findById(int id);

    List<Income> findAll();

    List<Income> findAllUserIncomes(int id);

    List<Income> findAllGroupIncomes(int id);

    List<Income> getAllGroupIncomesForSpecificUser(int userId, int groupId);

    Income save(Income income, int userId);
    void updateRepetitionFlow(int id, RepetitionFlow repetitionFlow);
    List<Income> findAllRecurringIncome();
    List<Income> getRecurringIncomeTransactions();
}