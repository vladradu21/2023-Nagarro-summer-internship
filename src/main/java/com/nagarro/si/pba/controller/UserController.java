package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/users")
public class UserController implements UserApi {
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDTO getById(int id) {
        LOGGER.info("Getting user by Id: {}", id);
        return userService.getById(id);
    }

    @Override
    public UserDTO getByEmail(String email) {
        LOGGER.info("Getting user by email: {}", email);
        return userService.getByEmail(email);
    }

    @Override
    public List<UserDTO> getAll() {
        LOGGER.info("Getting all users");
        return userService.getAll();
    }
}