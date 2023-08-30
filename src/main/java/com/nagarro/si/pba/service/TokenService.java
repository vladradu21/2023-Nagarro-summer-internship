package com.nagarro.si.pba.service;

import com.nagarro.si.pba.model.Token;
import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    Token save(Token token);
    void findToken(String token);
    int extractUserId(String token);
    void delete(String token);

}
