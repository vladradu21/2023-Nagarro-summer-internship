package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.dto.LoginDTO;
import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.UnauthorizedException;
import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.security.PasswordEncoder;
import com.nagarro.si.pba.service.LoginService;
import com.nagarro.si.pba.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    private final JWTGenerator jwtGenerator;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    public LoginServiceImpl(JWTGenerator jwtGenerator, UserRepository userRepository, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.jwtGenerator = jwtGenerator;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String login(LoginDTO loginDTO) {
        LOGGER.info("Attempting login for user: {}", loginDTO.username());
        User user = userRepository.findByUsername(loginDTO.username())
                .orElseThrow(() -> {
                    LOGGER.warn("Login failed");
                    return new UnauthorizedException(ExceptionMessage.UNAUTHORIZED.format());
                });

        if (!passwordEncoder.checkPasswordMatch(loginDTO.password(), user.getPassword()) ||
                !user.getIsVerified()) {
            LOGGER.warn("Login failed");
            throw new UnauthorizedException(ExceptionMessage.UNAUTHORIZED.format());
        }

        String tokenString = jwtGenerator.generateJwtForLogin(user.getId(), loginDTO.username());
        Token token = jwtGenerator.generateTokenObject(tokenString);
        tokenService.save(token);

        LOGGER.info("Login successful for user: {}", loginDTO.username());
        return tokenString;
    }
}
