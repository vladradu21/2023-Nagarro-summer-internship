package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.GroupDTO;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.mapper.GroupMapper;
import com.nagarro.si.pba.model.Group;
import com.nagarro.si.pba.repository.GroupRepository;
import com.nagarro.si.pba.repository.UserGroupRoleRepository;
import com.nagarro.si.pba.service.impl.GroupServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private UserGroupRoleRepository userGroupRoleRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Test
    void testSaveGroup() {
        Group groupToSave = TestData.returnGroup();
        GroupDTO groupDTO = TestData.returnGroupDTO();
        Group savedGroup = TestData.returnGroup();
        when(groupMapper.dtoToEntity(groupDTO)).thenReturn(groupToSave);
        when(groupRepository.save(groupToSave)).thenReturn(savedGroup);
        when(groupMapper.entityToDTO(savedGroup)).thenReturn(groupDTO);

        GroupDTO result = groupService.save(groupDTO);

        assertEquals(groupDTO, result);
    }

    @Test
    void testGetGroupByIdExisting() {
        Group group = TestData.returnGroup();
        GroupDTO groupDTO = TestData.returnGroupDTO();
        when(groupRepository.findById(groupDTO.id())).thenReturn(Optional.of(group));
        when(groupMapper.entityToDTO(group)).thenReturn(groupDTO);

        GroupDTO result = groupService.getById(groupDTO.id());

        assertEquals(groupDTO, result);
    }

    @Test
    void testGetGroupByIdNotFound() {
        when(groupRepository.findById(404)).thenReturn(Optional.empty());

        assertThrows(PbaNotFoundException.class, () -> groupService.getById(404));
    }

    @Test
    void testGetAllGroups() {
        Group group = TestData.returnGroup();
        GroupDTO groupDTO = TestData.returnGroupDTO();
        when(groupRepository.findAll()).thenReturn(Collections.singletonList(group));
        when(groupMapper.entityToDTO(Collections.singletonList(group))).thenReturn(Collections.singletonList(groupDTO));

        List<GroupDTO> result = groupService.getAll();

        assertEquals(1, result.size());
        assertEquals(groupDTO, result.get(0));
    }

    @Test
    void testDeleteGroup() {
        int groupId = 1;

        groupService.delete(groupId);

        verify(groupRepository).delete(groupId);
        verify(userGroupRoleRepository).delete(groupId);
    }

    @Test
    void testGetGroupIdByNameAndUserId() {
        String groupName = "test";
        int userId = 1;
        int groupId = 1;
        when(groupRepository.findGroupIdByNameAndUserId(groupName, userId)).thenReturn(groupId);

        int result = groupService.getGroupIdByNameAndUserId(groupName, userId);

        assertEquals(groupId, result);
    }
}