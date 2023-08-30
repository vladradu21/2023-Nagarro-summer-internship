package com.nagarro.si.pba.security;


import com.nagarro.si.pba.exceptions.UnauthorizedException;
import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.utils.TestData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JWTGeneratorTest {
    private static final long EXPIRATION_TIME_LOGIN = TimeUnit.MINUTES.toMillis(30);
    private static final long EXPIRATION_TIME_REGISTER = TimeUnit.HOURS.toMillis(24);
    private static final String secretKey = "2zCGWqdyNBzitg9NijPcA61819rfeSXg8ETEg3z8DNKmRgFn0EJa5YkBVf7QIcIA";
    private static final int userId = 1;
    private static final String username = "testusername";

    private JWTGenerator jwtGenerator;
    private String jwtForLogin;
    private String jwtForRegistration;
    private final String expiredJwt = TestData.ReturnExpiredToken();

    @BeforeEach
    public void setup() {
        jwtGenerator = new JWTGenerator();
        ReflectionTestUtils.setField(jwtGenerator, "secretKey", secretKey);
        jwtForLogin = jwtGenerator.generateJwtForLogin(userId, username);
        jwtForRegistration = jwtGenerator.generateJwtForRegistration(userId, username);
    }

    @Test
    public void testGenerateJwtForRegistration() {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtForRegistration)
                .getBody();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_REGISTER);

        assertEquals("registration", claims.getSubject());
        assertEquals(userId, claims.get("userId"));
        assertEquals(username, claims.get("username"));
        assertEquals(expiryDate.getTime(), claims.getExpiration().getTime(), 1);
    }

    @Test
    public void testGenerateJwtForLogin() {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtForLogin)
                .getBody();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_LOGIN);

        assertEquals(username, claims.getSubject());
        assertEquals(userId, claims.get("userId"));
        assertEquals(expiryDate.getTime(), claims.getExpiration().getTime(), 10000);
    }

    @Test
    public void testGenerateTokenObject() {
        Token token = jwtGenerator.generateTokenObject(jwtForLogin);

        assertEquals(jwtForLogin, token.getToken());
    }

    @Test
    public void testGetClaimsFromJwt() {
        Claims claims = jwtGenerator.getClaimsFromJwt(jwtForRegistration);

        assertEquals("registration", claims.getSubject());
        assertEquals(userId, claims.get("userId"));
        assertEquals(username, claims.get("username"));
    }

    @Test
    public void testGetClaimsFromJwtIfTokenExpired() {
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> jwtGenerator.getClaimsFromJwt(expiredJwt));

        assertEquals("The token has expired", exception.getMessage());
    }

    @Test
    public void testIsTokenExpiredForExpiredToken() {
        assertTrue(jwtGenerator.isTokenExpired(expiredJwt));
    }

    @Test
    public void testIsTokenExpiredForNotExpiredToken() {
        assertFalse(jwtGenerator.isTokenExpired(jwtForLogin));
    }
}

