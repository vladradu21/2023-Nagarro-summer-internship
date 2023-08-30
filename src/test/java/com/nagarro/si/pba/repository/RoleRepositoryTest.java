package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Role;
import com.nagarro.si.pba.model.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("inttest")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByType() {
        Optional<Role> adminRole = roleRepository.findByType(RoleType.ADMIN.toString());
        Optional<Role> guestRole = roleRepository.findByType(RoleType.GUEST.toString());
        Optional<Role> regularRole = roleRepository.findByType(RoleType.REGULAR.toString());

        assertTrue(adminRole.isPresent());
        assertTrue(guestRole.isPresent());
        assertTrue(regularRole.isPresent());
    }
}