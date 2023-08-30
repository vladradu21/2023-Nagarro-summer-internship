package com.nagarro.si.pba.service;

import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.repository.TokenRepository;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.service.impl.TokenServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    private final String tokenStr = "testTokenStr";
    private final Token token = new Token(tokenStr);
    private TokenService tokenService;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private JWTGenerator jwtGenerator;

    @BeforeEach
    public void setup() {
        tokenService = new TokenServiceImpl(tokenRepository, jwtGenerator);
    }

    @Test
    void shouldSaveToken() {
        Token token = TestData.returnNewToken();
        when(tokenRepository.save(token)).thenReturn(token);

        Token savedToken = tokenService.save(token);

        assertNotNull(savedToken);
        assertEquals(1, token.getId());
        verify(tokenRepository).save(token);
    }

    @Test
    void shouldThrowVerificationTokenNotFoundWhenTokenDoesNotExist() {
        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.empty());

        PbaNotFoundException exception = assertThrows(
                PbaNotFoundException.class,
                () -> tokenService.findToken(tokenStr)
        );
        assertEquals(ExceptionMessage.VERIFICATION_TOKEN_NOT_FOUND_OR_ALREADY_USED.format(), exception.getMessage());
        verify(tokenRepository).findByToken(tokenStr);
        verify(jwtGenerator, never()).isTokenExpired(tokenStr);
    }

    @Test
    void shouldReturnUserIdWhenTokenIsValid() {
        int expectedUserId = 1;

        Claims claims = Mockito.mock(Claims.class);
        when(claims.get("userId", Integer.class)).thenReturn(expectedUserId);
        when(jwtGenerator.getClaimsFromJwt(tokenStr)).thenReturn(claims);

        int userId = tokenService.extractUserId(tokenStr);

        assertEquals(expectedUserId, userId);
        verify(jwtGenerator).getClaimsFromJwt(tokenStr);
        verify(claims).get("userId", Integer.class);
    }

    @Test
    void shouldDeleteToken() {
        doNothing().when(tokenRepository).delete(tokenStr);

        assertDoesNotThrow(() -> tokenService.delete(tokenStr));
        verify(tokenRepository).delete(tokenStr);
    }
}