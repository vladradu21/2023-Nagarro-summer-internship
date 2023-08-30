package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Token;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository {
    Token save(Token token);
    Optional<Token> findByToken(String token);
    List<Token> findAll();
    void delete(String token);
}
