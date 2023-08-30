package com.nagarro.si.pba.mapper;

import com.nagarro.si.pba.dto.GroupDTO;
import com.nagarro.si.pba.model.Group;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GroupMapper {
    GroupDTO entityToDTO(Group group);

    List<GroupDTO> entityToDTO(List<Group> groups);

    Group dtoToEntity(GroupDTO groupDTO);

    List<Group> dtoToEntity(List<GroupDTO> groupsDTO);
}
