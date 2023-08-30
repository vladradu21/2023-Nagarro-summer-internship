package com.nagarro.si.pba.repository.repositoryImpl;

import com.nagarro.si.pba.model.Role;
import com.nagarro.si.pba.repository.BaseRepository;
import com.nagarro.si.pba.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepositoryImpl extends BaseRepository implements RoleRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoleRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Role> findByType(String type) {
        String query = "SELECT * FROM role WHERE name = ?";
        List<Role> result = jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(Role.class),
                type);
        return result.stream().findFirst();
    }
}