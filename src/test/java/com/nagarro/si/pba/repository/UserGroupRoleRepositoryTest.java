package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Group;
import com.nagarro.si.pba.model.Role;
import com.nagarro.si.pba.model.RoleType;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("inttest")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:script/cleanup.sql")
class UserGroupRoleRepositoryTest {

    @Autowired
    private UserGroupRoleRepository userGroupRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user;

    private Group group;

    @BeforeEach
    void setup() {
        user = userRepository.save(TestData.returnUserForDonJoe());
        group = groupRepository.save(TestData.returnGroup());
        Optional<Role> role = roleRepository.findByType(RoleType.ADMIN.toString());
        role.ifPresent(value -> userGroupRoleRepository.assignUserGroupRole(user.getId(), group.getId(), value.getId()));
    }

    @Test
    void testAssignUserGroupRole() {
        int groupId = groupRepository.findGroupIdByNameAndUserId(group.getName(), user.getId());

        assertEquals(group.getId(), groupId);
    }

    @Test
    void testDeleteGroup() {
        userGroupRoleRepository.delete(group.getId());

        int groupId = groupRepository.findGroupIdByNameAndUserId(group.getName(), user.getId());

        assertEquals(-1, groupId);
    }

    @Test
    void testDeleteGroupUser() {
        userGroupRoleRepository.removeUserFromGroup(group.getId(), user.getId());

        int groupId = groupRepository.findGroupIdByNameAndUserId(group.getName(), user.getId());

        assertEquals(-1, groupId);
    }
}