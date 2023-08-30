package com.nagarro.si.pba.security;

import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.UnauthorizedException;
import com.nagarro.si.pba.model.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JWTGenerator {
    private static final long EXPIRATION_TIME_LOGIN = TimeUnit.MINUTES.toMillis(30);
    private static final long EXPIRATION_TIME_REGISTER = TimeUnit.HOURS.toMillis(24);

    @Value("${jwt.secret-key}")
    private String secretKey;
    private static final Logger logger = LoggerFactory.getLogger(JWTGenerator.class);

    public String generateJwtForRegistration(int id, String username) {
        logger.info("Generating JWT for registration for user: {}", username);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_REGISTER);

        return Jwts.builder()
                .setSubject("registration")
                .claim("userId", id)
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(secretKey))
                .compact();
    }

    public String generateJwtForLogin(int id, String username) {
        logger.info("Generating JWT for login for user: {}", username);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_LOGIN);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", id)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(secretKey))
                .compact();
    }

    public Token generateTokenObject(String jwt) {
        return new Token(jwt);
   }

    public Claims getClaimsFromJwt(String jwt) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT detected: {}", jwt);
            throw new UnauthorizedException(ExceptionMessage.EXPIRED_TOKEN.format());
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = getClaimsFromJwt(token).getExpiration();
            return expiration.before(new Date());
        } catch (UnauthorizedException e) {
            logger.error("Unauthorized exception while checking token expiration");
            return true;
        }
    }

    private Key getSignInKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}