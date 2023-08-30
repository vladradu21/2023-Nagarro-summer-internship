package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/verification")
public class VerificationController implements VerificationApi {
    private final TokenService tokenService;
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(VerificationController.class);

    public VerificationController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        int userId = tokenService.extractUserId(token);
        LOGGER.info("Verifying account for user with id: {}", userId);
        tokenService.findToken(token);
        userService.setVerifiedAccount(userId);
        tokenService.delete(token);

        LOGGER.info("Account verification successful for user with id: {}", userId);
        return ResponseEntity.ok("Account has been verified successfully.");
    }
}