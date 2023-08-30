package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.PasswordDTO;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.service.EmailSenderService;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.UserService;
import com.nagarro.si.pba.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping
public class ResetPasswordController implements ResetPasswordApi {
    private final TokenService tokenService;
    private final UserService userService;
    private final JWTGenerator jwtGenerator;
    private final EmailSenderService emailSenderService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordController.class);

    public ResetPasswordController(TokenService tokenService, UserService userService, JWTGenerator jwtGenerator, EmailSenderService emailSenderService) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.jwtGenerator = jwtGenerator;
        this.emailSenderService = emailSenderService;
    }

    @Override
    public void triggerResetPassword(String emailJson) {
        String email = JsonUtils.extractFieldFromJson("email", emailJson);
        LOGGER.info("Triggering reset password for user with email: {}", email);
        UserDTO user = userService.getByEmail(email);
        String token = jwtGenerator.generateJwtForRegistration(user.id(), user.username());
        emailSenderService.sendResetPasswordEmail(user, token);
        Token tokenObject = jwtGenerator.generateTokenObject(token);
        tokenService.save(tokenObject);
        LOGGER.info("Reset password triggered successfully for user with email: {}", email);
    }

    @Override
    public String resetPassword(String token, PasswordDTO passwordDTO) {
        int userId = tokenService.extractUserId(token);
        LOGGER.info("Resetting password for user with id: {}", userId);
        tokenService.findToken(token);
        userService.updatePassword(userId, passwordDTO.password());
        tokenService.delete(token);

        LOGGER.info("Password reset successfully for user with id: {}", userId);
        return "Password has been reset successfully.";
    }
}
