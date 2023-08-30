package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.repository.UserGroupRoleRepository;
import com.nagarro.si.pba.service.UserGroupRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserGroupRoleServiceImpl implements UserGroupRoleService {

    private final UserGroupRoleRepository userGroupRoleRepository;

    @Autowired
    public UserGroupRoleServiceImpl(UserGroupRoleRepository userGroupRoleRepository) {
        this.userGroupRoleRepository = userGroupRoleRepository;
    }

    @Override
    public void assignUserGroupRole(int userId, int groupId, int roleId) {
        userGroupRoleRepository.assignUserGroupRole(userId, groupId, roleId);
    }

    @Override
    public void delete(int groupId) {
        userGroupRoleRepository.delete(groupId);
    }

    @Override
    public void removeUserFromGroup(int groupId, int userId) {
        userGroupRoleRepository.removeUserFromGroup(groupId, userId);
    }
}
