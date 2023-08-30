package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository {
    Optional<Role> findByType(String type);
}
