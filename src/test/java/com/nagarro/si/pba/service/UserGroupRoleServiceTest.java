package com.nagarro.si.pba.service;

import com.nagarro.si.pba.repository.UserGroupRoleRepository;
import com.nagarro.si.pba.service.impl.UserGroupRoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserGroupRoleServiceTest {
    @Mock
    private UserGroupRoleRepository userGroupRoleRepository;

    @InjectMocks
    private UserGroupRoleServiceImpl userGroupRoleService;

    int userId = 1;
    int groupId = 1;
    int roleId = 1;

    @Test
    void testAssignUserGroupRole() {
        userGroupRoleService.assignUserGroupRole(userId, groupId, roleId);

        verify(userGroupRoleRepository).assignUserGroupRole(userId, groupId, roleId);
    }

    @Test
    void testDeleteByGroupId() {
        userGroupRoleService.delete(groupId);

        verify(userGroupRoleRepository).delete(groupId);
    }

    @Test
    void testDeleteByGroupIdAndUserId() {
        userGroupRoleService.removeUserFromGroup(groupId, userId);

        verify(userGroupRoleRepository).removeUserFromGroup(groupId, userId);
    }
}