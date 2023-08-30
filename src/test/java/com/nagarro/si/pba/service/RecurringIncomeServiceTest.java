package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.IncomeDTO;
import com.nagarro.si.pba.exceptions.PbaConflictException;
import com.nagarro.si.pba.mapper.IncomeMapper;
import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.repository.IncomeRepository;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.service.impl.RecurringIncomeServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecurringIncomeServiceTest {
    @Mock
    private IncomeRepository incomeRepository;
    @Mock
    private IncomeMapper incomeMapper;
    @Mock
    private BnrCurrencyService bnrCurrencyService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RecurringIncomeServiceImpl recurringIncomeService;

    @Test
    void testSaveIncome() {
        IncomeDTO incomeDTO = TestData.returnRecurringIncomeDTO();
        Income income = TestData.returnRecurringIncomeEntity();
        Income savedIncome = TestData.returnRecurringIncomeEntity();
        savedIncome.setId(1);

        when(incomeMapper.dtoToEntity(incomeDTO)).thenReturn(income);
        when(incomeRepository.save(income, 1)).thenReturn(savedIncome);
        IncomeDTO savedIncomeDTO = TestData.returnOneTimeIncomeDTO();

        when(incomeMapper.entityToDTO(savedIncome)).thenReturn(savedIncomeDTO);
        when(userRepository.getDefaultCurrency(1)).thenReturn(Currency.RON);

        IncomeDTO resultDTO = recurringIncomeService.saveIncome(incomeDTO, 1);

        assertEquals(resultDTO.id(), savedIncomeDTO.id());
    }

    @Test
    void testSaveIncome_throwsExceptionWhenIncomeIsNotRecurring() {
        IncomeDTO incomeDTO = TestData.returnOneTimeIncomeDTO();

        assertThrows(PbaConflictException.class, () -> recurringIncomeService.saveIncome(incomeDTO, 1));
    }
}