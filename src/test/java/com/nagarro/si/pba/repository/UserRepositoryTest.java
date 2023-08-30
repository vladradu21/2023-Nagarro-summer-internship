package com.nagarro.si.pba.repository;

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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("inttest")
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserRepositoryTest extends AbstractMySQLContainer {
    private User user;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        user = TestData.returnUserForDonJoe();
        userRepository.save(user);
    }

    @Test
    void testSaveUser() {
        assertNotEquals(0, user.getId());
        assertEquals("donjoe", user.getUsername());
        assertEquals("donjoe@gmail.com", user.getEmail());
        assertEquals("don", user.getFirstName());
        assertEquals("joe", user.getLastName());
        assertEquals("colombia", user.getCountry());
        assertEquals(38, user.getAge());
    }

    @Test
    void testFindById() {
        Optional<User> userById = userRepository.findById(user.getId());

        assertTrue(userById.isPresent());
        assertEquals(user.getId(), userById.get().getId());
    }

    @Test
    void testFindByUsername() {
        Optional<User> userByName = userRepository.findByUsername(user.getUsername());

        assertTrue(userByName.isPresent());
        assertEquals(user.getUsername(), userByName.get().getUsername());
    }

    @Test
    void testFindByEmail() {
        Optional<User> userByEmail = userRepository.findByEmail(user.getEmail());

        assertTrue(userByEmail.isPresent());
        assertEquals(user.getEmail(), userByEmail.get().getEmail());
    }

    @Test
    void testFindAll() {
        List<User> users = userRepository.findAll();

        assertEquals(1, users.size());
    }
}
