package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.RegisterDTO;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.service.EmailSenderService;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegisterController implements RegisterApi {
    private final UserService userService;
    private final EmailSenderService emailSenderService;
    private final JWTGenerator jwtGenerator;
    private final TokenService tokenService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

    public RegisterController(UserService userService, EmailSenderService emailSenderService, JWTGenerator jwtGenerator, TokenService tokenService) {
        this.userService = userService;
        this.emailSenderService = emailSenderService;
        this.jwtGenerator = jwtGenerator;
        this.tokenService = tokenService;
    }

    @Override
    public UserDTO create(RegisterDTO registerDTO) {
        LOGGER.info("Creating a new user registration");
        UserDTO userDTO = userService.save(registerDTO);
        String tokenString = jwtGenerator.generateJwtForRegistration(userDTO.id(), userDTO.username());
        Token token = jwtGenerator.generateTokenObject(tokenString);
        emailSenderService.sendRegistrationEmail(userDTO, tokenString);
        tokenService.save(token);
        LOGGER.info("User registration created successfully");
        return userDTO;
    }
}