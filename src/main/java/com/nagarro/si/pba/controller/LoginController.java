package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.LoginDTO;
import com.nagarro.si.pba.service.LoginService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController implements LoginApi {
    private final LoginService loginService;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginDTO loginDTO) {
        LOGGER.info("User attempting login: {}", loginDTO.username());

        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("token", loginService.login(loginDTO));

        LOGGER.info("User logged in successfully: {}", loginDTO.username());
        return ResponseEntity.ok(jsonResponse);
    }
}
