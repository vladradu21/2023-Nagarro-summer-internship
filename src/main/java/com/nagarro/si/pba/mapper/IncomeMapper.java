package com.nagarro.si.pba.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.nagarro.si.pba.dto.IncomeDTO;
import com.nagarro.si.pba.model.Income;

@Mapper(componentModel = "spring")
public interface IncomeMapper {
    IncomeDTO entityToDTO(Income income);

    List<IncomeDTO> entityToDTO(List<Income> incomes);

    Income dtoToEntity(IncomeDTO incomeDTO);

    List<Income> dtoToEntity(List<IncomeDTO> incomesDTO);
}