package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.dto.GroupDTO;
import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.mapper.GroupMapper;
import com.nagarro.si.pba.model.Group;
import com.nagarro.si.pba.repository.GroupRepository;
import com.nagarro.si.pba.repository.UserGroupRoleRepository;
import com.nagarro.si.pba.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    private final GroupMapper groupMapper;

    private final UserGroupRoleRepository userGroupRoleRepository;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository, GroupMapper groupMapper, UserGroupRoleRepository userGroupRoleRepository) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.userGroupRoleRepository = userGroupRoleRepository;
    }

    @Override
    public GroupDTO save(GroupDTO groupDTO) {
        Group groupToSave = groupMapper.dtoToEntity(groupDTO);
        return groupMapper.entityToDTO(groupRepository.save(groupToSave));
    }

    @Override
    public GroupDTO getById(int id) {
        return groupRepository.findById(id)
                .map(groupMapper::entityToDTO)
                .orElseThrow(() -> new PbaNotFoundException(ExceptionMessage.GROUP_NOT_FOUND.format(id)));
    }

    @Override
    public List<GroupDTO> getAll() {
        List<Group> groups = groupRepository.findAll();
        return groupMapper.entityToDTO(groups);
    }

    @Override
    public void delete(int id) {
        groupRepository.delete(id);
        userGroupRoleRepository.delete(id);
    }

    @Override
    public int getGroupIdByNameAndUserId(String groupName, int userId) {
        return groupRepository.findGroupIdByNameAndUserId(groupName, userId);
    }
}
