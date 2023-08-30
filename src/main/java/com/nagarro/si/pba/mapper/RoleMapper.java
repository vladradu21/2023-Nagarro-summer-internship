package com.nagarro.si.pba.mapper;

import com.nagarro.si.pba.dto.RoleDTO;
import com.nagarro.si.pba.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {
    RoleDTO entityToDTO(Role role);

    Role dtoToEntity(RoleDTO roleDTO);
}
