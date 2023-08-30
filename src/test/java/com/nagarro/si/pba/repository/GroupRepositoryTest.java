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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("inttest")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:script/cleanup.sql")
class GroupRepositoryTest extends AbstractMySQLContainer {
    private Group group;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserGroupRoleRepository userGroupRoleRepository;

    @BeforeEach
    public void setup() {
        group = groupRepository.save(TestData.returnGroup());
    }

    @Test
    void testSaveGroup() {
        assertNotEquals(0, group.getId());
        assertEquals("test", group.getName());
    }

    @Test
    void testFindAll() {
        List<Group> groups = groupRepository.findAll();
        assertEquals(1, groups.size());
    }

    @Test
    void testFindById() {
        Optional<Group> findGroup = groupRepository.findById(group.getId());

        assertTrue(findGroup.isPresent());
        assertEquals(findGroup.get(), group);
    }

    @Test
    void testFindById_WrongId() {
        Optional<Group> findGroup = groupRepository.findById(404);

        assertFalse(findGroup.isPresent());
    }

    @Test
    void testDelete() {
        groupRepository.delete(group.getId());
        Optional<Group> findGroup = groupRepository.findById(group.getId());

        assertFalse(findGroup.isPresent());
    }

    @Test
    void testFindGroupIdByNameAndUserId() {
        User user = userRepository.save(TestData.returnUserForDonJoe());
        Optional<Role> role = roleRepository.findByType(RoleType.ADMIN.toString());
        role.ifPresent(value -> userGroupRoleRepository.assignUserGroupRole(user.getId(), group.getId(), value.getId()));

        int groupId = groupRepository.findGroupIdByNameAndUserId(group.getName(), user.getId());

        assertEquals(group.getId(), groupId);
    }
    @Test
    void testUpdateBalance() {
        double initialBalance = group.getBalance();
        double addedBalance = 50.0;
        double expectedBalance = initialBalance + addedBalance;

        groupRepository.updateBalance(expectedBalance, group.getId());

        Optional<Group> updatedGroup = groupRepository.findById(group.getId());
        assertTrue(updatedGroup.isPresent(), "Updated group should be present");
        assertEquals(expectedBalance, updatedGroup.get().getBalance(), 0.01, "Balance should be updated correctly");
    }

}