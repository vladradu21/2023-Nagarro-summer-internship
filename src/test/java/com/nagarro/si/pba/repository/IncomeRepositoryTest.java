package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Group;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.RepetitionFlow;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("inttest")
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class IncomeRepositoryTest extends AbstractMySQLContainer {
    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;
    private Income savedIncome;
    private User savedUser;
    private Group savedGroup;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(TestData.returnUserForBobDumbledore());
        savedGroup = groupRepository.save(TestData.returnGroup());
        Income income = TestData.returnOneTimeIncomeEntity();
        income.setGroupId(savedGroup.getId());
        savedIncome = incomeRepository.save(income, savedUser.getId());
    }

    @Test
    void testFindById() {
        Optional<Income> incomeById = incomeRepository.findById(savedIncome.getId());

        assertTrue(incomeById.isPresent());
        assertEquals(savedIncome.getId(), incomeById.get().getId());
    }

    @Test
    void testFindAll() {
        List<Income> incomes = incomeRepository.findAll();
        assertFalse(incomes.isEmpty());
        assertEquals(1, incomes.size());
        assertEquals(savedIncome.getId(), incomes.get(0).getId());
    }

    @Test
    void testFindAllUserIncomes() {
        List<Income> incomes = incomeRepository.findAllUserIncomes(savedUser.getId());
        assertFalse(incomes.isEmpty());
        assertEquals(1, incomes.size());
        assertEquals(savedIncome.getId(), incomes.get(0).getId());
    }

    @Test
    void testFindAllGroupIncomes() {
        List<Income> incomes = incomeRepository.findAllGroupIncomes(savedGroup.getId());
        assertFalse(incomes.isEmpty());
        assertEquals(1, incomes.size());
        assertEquals(savedIncome.getId(), incomes.get(0).getId());
    }

    @Test
    void testGetAllGroupIncomesForSpecificUser() {
        List<Income> incomes = incomeRepository.getAllGroupIncomesForSpecificUser(savedUser.getId(), savedGroup.getId());
        assertFalse(incomes.isEmpty());
        assertEquals(1, incomes.size());
        assertEquals(savedIncome.getId(), incomes.get(0).getId());
    }

    @Test
    void testFindAllRecurringIncome() {
        incomeRepository.save(TestData.returnRecurringIncomeEntity(), savedUser.getId());
        List<Income> recurringIncomes = incomeRepository.findAllRecurringIncome();

        assertFalse(recurringIncomes.isEmpty());

        for (Income income : recurringIncomes) {
            assertFalse(RepetitionFlow.NONE.equals(income.getRepetitionFlow()));
        }
    }

    @Test
    void testUpdateRepetitionFlow() {
        RepetitionFlow newFlow = RepetitionFlow.DAILY;
        incomeRepository.updateRepetitionFlow(savedIncome.getId(), newFlow);

        Optional<Income> updatedIncome = incomeRepository.findById(savedIncome.getId());
        assertTrue(updatedIncome.isPresent());
        assertEquals(newFlow, updatedIncome.get().getRepetitionFlow());
    }

    @Test
    void testGetRecurringIncomeTransactions() {
        Income income = TestData.returnIncome();
        income.setAddedDate(LocalDateTime.now());
        incomeRepository.save(income, savedUser.getId());
        List<Income> recurringTransactions = incomeRepository.getRecurringIncomeTransactions();

        assertFalse(recurringTransactions.isEmpty());

        for (Income incomes : recurringTransactions) {
            assertEquals(RepetitionFlow.ANNUALLY, incomes.getRepetitionFlow());
        }
    }
}