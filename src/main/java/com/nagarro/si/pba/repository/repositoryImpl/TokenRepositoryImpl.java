package com.nagarro.si.pba.repository.repositoryImpl;

import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.repository.BaseRepository;
import com.nagarro.si.pba.repository.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class TokenRepositoryImpl extends BaseRepository implements TokenRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenRepositoryImpl.class);

    @Autowired
    public TokenRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Token save(Token token) {
        LOGGER.info("Saving new token");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO token (token) VALUES (?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, token.getToken());
            return ps;
        }, keyHolder);

        int generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        token.setId(generatedId);

        LOGGER.info("Saved new token with ID: {}", generatedId);
        return token;
    }

    @Override
    public Optional<Token> findByToken(String token) {
        LOGGER.info("Finding token by value: {}", token);
        return handleEmptyResult(() -> Optional.ofNullable(jdbcTemplate.query(
                "SELECT * FROM token WHERE token = ?",
                rs -> {
                    if (rs.next()) {
                        return extractTokenFromResultSet(rs);
                    }
                    return null;
                },
                token
        )));
    }

    @Override
    public List<Token> findAll() {
        LOGGER.info("Fetching all tokens");
        String query = "select * from token";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Token.class));
    }

    @Override
    public void delete(String token) {
        LOGGER.warn("Deleting token with value: {}", token);
        String sql = "DELETE FROM token WHERE token = ?";
        int rowsAffected = jdbcTemplate.update(sql, token);
        if (rowsAffected == 0) {
            throw new DataAccessException("No rows affected when trying to delete token: " + token) {
            };
        }
    }

    private Token extractTokenFromResultSet(ResultSet rs) throws SQLException {
        Token token = new Token();
        token.setId(rs.getInt("id"));
        token.setToken(rs.getString("token"));
        return token;
    }
}