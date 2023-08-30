package com.nagarro.si.pba.repository.repositoryImpl;

import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.BaseRepository;
import com.nagarro.si.pba.repository.UserRepository;
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
public class UserRepositoryImpl extends BaseRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @Autowired
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findById(int id) {
        LOGGER.info("Finding user by ID: {}", id);
        return handleEmptyResult(() -> Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM user WHERE id = ?", new BeanPropertyRowMapper<>(User.class), id)));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        LOGGER.info("Finding user by username: {}", username);
        return handleEmptyResult(() -> Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM user WHERE username = ?", new BeanPropertyRowMapper<>(User.class), username)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        LOGGER.info("Finding user by email: {}", email);
        return handleEmptyResult(() -> Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM user WHERE email = ?", new BeanPropertyRowMapper<>(User.class), email)));
    }

    @Override
    public List<User> findAll() {
        LOGGER.info("Fetching all users");
        String query = "select * from user";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public User save(User user) {
        LOGGER.info("Saving new user: {}", user.getEmail());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "insert into user (email, username, password, firstName, lastName, country, age, isVerified, balance, default_currency) values(?,?,?,?,?,?,?,?,?,?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getLastName());
            ps.setString(6, user.getCountry());
            ps.setInt(7, user.getAge());
            ps.setBoolean(8, user.getIsVerified());
            ps.setDouble(9, user.getBalance());
			ps.setString(10, user.getDefaultCurrency().toString());
            return ps;
        }, keyHolder);

        int generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        user.setId(generatedId);
        LOGGER.info("Saved new user with ID: {}", generatedId);
        return user;
    }

    @Override
    public User update(User user) {
        LOGGER.info("Updating user with ID: {}", user.getId());
        String query = """
                UPDATE user\s
                SET email = ?, username = ?, password = ?, firstName = ?, lastName = ?, country = ?, age = ?, isVerified = ?, balance = ?, default_currency = ?
                WHERE id = ?""";
        jdbcTemplate.update(query, user.getEmail(), user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getCountry(), user.getAge(), user.getIsVerified(), user.getBalance(), user.getDefaultCurrency().toString(), user.getId());
        return user;
    }

    @Override
    public void delete(int userId) {
        LOGGER.warn("Deleting user with ID: {}", userId);
        String query = "DELETE FROM user WHERE id = ?";
        jdbcTemplate.update(query, userId);
    }

    @Override
    public void updateBalance(double balance, int userId) {
        String sql = "UPDATE user SET balance = ? WHERE id = ?";
        jdbcTemplate.update(sql, balance, userId);
    }

    @Override
    public Currency getDefaultCurrency(int userId) {
        String query = "select default_currency from user where id = ?";
        return jdbcTemplate.queryForObject(query, Currency.class, userId);
    }
}