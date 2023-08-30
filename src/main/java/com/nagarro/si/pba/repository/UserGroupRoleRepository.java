package com.nagarro.si.pba.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRoleRepository {
    void assignUserGroupRole(int userId, int groupId, int roleId);

    void delete(int groupId);

    void removeUserFromGroup(int groupId, int userId);
}