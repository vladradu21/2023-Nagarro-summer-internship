package com.nagarro.si.pba.repository.repositoryImpl;

import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.Group;
import com.nagarro.si.pba.repository.BaseRepository;
import com.nagarro.si.pba.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class GroupRepositoryImpl extends BaseRepository implements GroupRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupRepositoryImpl.class);

    @Autowired
    public GroupRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Group save(Group group) {
        LOGGER.info("Saving new group: {}", group.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO `group` (name, balance, default_currency) VALUES (?,?,?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, group.getName());
            ps.setDouble(2, group.getBalance());
            ps.setString(3, group.getDefaultCurrency().toString());
            return ps;
        }, keyHolder);

        int generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        group.setId(generatedId);

        LOGGER.info("Saved new group with ID: {}", generatedId);
        return group;
    }

    @Override
    public List<Group> findAll() {
        LOGGER.info("Fetching all groups");
        String query = "SELECT * FROM `group`";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Group.class));

    }

    @Override
    public Optional<Group> findById(int id) {
        LOGGER.info("Finding group by ID: {}", id);
        String query = "SELECT * FROM `group` WHERE id = ?";
        List<Group> result = jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(Group.class),
                id);
        return result.stream().findFirst();
    }

    @Override
    public void delete(int id) {
        LOGGER.warn("Deleting group with ID: {}", id);
        String categoryDeleteQuery = "DELETE FROM `group` WHERE id = ?";
        jdbcTemplate.update(categoryDeleteQuery, id);
    }
    @Override
    public void updateBalance(double balance, int groupId) {
        String sql = "UPDATE `group` SET balance = ? WHERE id = ?";
        jdbcTemplate.update(sql, balance, groupId);
    }

    @Override
    public int findGroupIdByNameAndUserId(String groupName, int userId) {
        String query = "SELECT g.id FROM `group` g " +
                "JOIN role_user_group rug ON g.id = rug.groupId " +
                "WHERE g.name = ? AND rug.userId = ?";
        List<Integer> result = jdbcTemplate.query(query, (rs, rowNum) -> rs.getInt("id"), groupName, userId);
        return result.isEmpty() ? -1 : result.get(0);
    }

    @Override
    public Currency getDefaultCurrency(int groupId) {
        String query = "SELECT default_currency FROM `group` WHERE id = ?";
        return jdbcTemplate.queryForObject(query, Currency.class, groupId);
    }
}