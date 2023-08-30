package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("inttest")
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TokenRepositoryTest extends AbstractMySQLContainer {
    @Autowired
    private TokenRepository tokenRepository;

    @BeforeEach
    public void setup() {
        Token testToken = TestData.returnNewToken();
        tokenRepository.save(testToken);
    }

    @Test
    void testFindByToken() {
        Optional<Token> tokenOpt = tokenRepository.findByToken("newToken");

        assertTrue(tokenOpt.isPresent());
        assertEquals("newToken", tokenOpt.get().getToken());
    }

    @Test
    void testFindByNonExistentToken() {
        Optional<Token> tokenOpt = tokenRepository.findByToken("nonExistentToken");
        assertFalse(tokenOpt.isPresent());
    }

    @Test
    void testSave() {
        tokenRepository.save(TestData.returnNewToken());

        Optional<Token> retrievedToken = tokenRepository.findByToken("newToken");
        assertTrue(retrievedToken.isPresent());
        assertEquals("newToken", retrievedToken.get().getToken());
    }

    @Test
    void testFindAll() {
        List<Token> tokens = tokenRepository.findAll();

        assertEquals(1, tokens.size());
        assertEquals("newToken", tokens.get(0).getToken());
    }

    @Test
    void testDelete() {
        tokenRepository.delete("newToken");
        Optional<Token> tokenOpt = tokenRepository.findByToken("newToken");
        assertFalse(tokenOpt.isPresent());
    }

    @Test
    void testDeleteNonExistentToken() {
        assertThrows(DataAccessException.class, () -> tokenRepository.delete("nonExistentToken"));
    }
}