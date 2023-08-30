package com.nagarro.si.pba.service;

import org.springframework.stereotype.Service;

@Service
public interface UserGroupRoleService {
    void assignUserGroupRole(int userId, int groupId, int roleId);

    void delete(int groupId);

    void removeUserFromGroup(int groupId, int userId);
}
