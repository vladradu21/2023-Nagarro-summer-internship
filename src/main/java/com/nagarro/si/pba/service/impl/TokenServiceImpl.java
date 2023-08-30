package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.repository.TokenRepository;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.service.TokenService;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final JWTGenerator jwtGenerator;
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    public TokenServiceImpl(TokenRepository tokenRepository, JWTGenerator jwtGenerator) {
        this.tokenRepository = tokenRepository;
        this.jwtGenerator = jwtGenerator;
    }

    @Override
    public Token save(Token token) {
        LOGGER.info("Saving token: {}", token.getToken());
        return tokenRepository.save(token);
    }

    @Override
    public void findToken(String token) {
        LOGGER.info("Finding token: {}", token);
        Token currentToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    LOGGER.warn("Token not found: {}", token);
                    return new PbaNotFoundException(ExceptionMessage.VERIFICATION_TOKEN_NOT_FOUND_OR_ALREADY_USED.format());
                });
    }

    @Override
    public int extractUserId(String token) {
        LOGGER.info("Extracting user ID from token: {}", token);
        Claims claims = jwtGenerator.getClaimsFromJwt(token);
        return claims.get("userId", Integer.class);
    }

    @Override
    public void delete(String token) {
        LOGGER.warn("Deleting token: {}", token);
        tokenRepository.delete(token);
    }

}