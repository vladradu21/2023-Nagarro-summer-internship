package com.nagarro.si.pba.repository.repositoryImpl;

import com.nagarro.si.pba.repository.BaseRepository;
import com.nagarro.si.pba.repository.UserGroupRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserGroupRoleRepositoryImpl extends BaseRepository implements UserGroupRoleRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserGroupRoleRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void assignUserGroupRole(int userId, int groupId, int roleId) {
        String insertQuery = "INSERT INTO role_user_group (userId, groupId, roleId) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertQuery, userId, groupId, roleId);
    }

    @Override
    public void delete(int groupId) {
        String deleteQuery = "DELETE FROM role_user_group WHERE groupId = ?";
        jdbcTemplate.update(deleteQuery, groupId);
    }

    @Override
    public void removeUserFromGroup(int groupId, int userId) {
        String deleteQuery = "DELETE FROM role_user_group WHERE groupId = ? AND userId = ?";
        jdbcTemplate.update(deleteQuery, groupId, userId);
    }
}
