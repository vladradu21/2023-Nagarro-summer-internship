package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.service.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {
    private static final String EMAIL_SUBJECT_REGISTRATION = "Account verification";
    private static final String EMAIL_SUBJECT_RESET_PASSWORD = "Reset password";
    private static final String EMAIL_BODY_RESET_TEMPLATE = "Hello %s %s,\n\nYour account is verified. You can reset the password using the token below.\n\n";
    private static final String EMAIL_BODY_REGISTER_TEMPLATE = "Hello %s %s,\n\nPlease verify your account by clicking on the following link:\n\n%s\n\nThe link is valid for 24 hours.";
    private final JavaMailSender javaMailSender;

    @Value("${spring.app.verifyTokenEndpoint}")
    private String verifyTokenEndpoint;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.app.url}")
    private String url;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderServiceImpl.class);

    @Autowired
    public EmailSenderServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendRegistrationEmail(UserDTO userDTO, String token) {
        LOGGER.info("Sending registration email to: {}", userDTO.email());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailUsername);
        message.setTo(userDTO.email());
        message.setSubject(EMAIL_SUBJECT_REGISTRATION);
        message.setText(generateEmailBody(EMAIL_BODY_REGISTER_TEMPLATE, userDTO.firstName(), userDTO.lastName(), token, verifyTokenEndpoint, null));
        javaMailSender.send(message);
        LOGGER.info("Registration email sent successfully to: {}", userDTO.email());
    }

    @Override
    public void sendResetPasswordEmail(UserDTO userDTO, String token) {
        LOGGER.info("Sending reset password email to: {}", userDTO.email());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailUsername);
        message.setTo(userDTO.email());
        message.setSubject(EMAIL_SUBJECT_RESET_PASSWORD);
        message.setText(generateEmailBody(EMAIL_BODY_RESET_TEMPLATE, userDTO.firstName(), userDTO.lastName(), token, null, "Your token is: " + token));
        javaMailSender.send(message);
        LOGGER.info("Reset password email sent successfully to: {}", userDTO.email());
    }

    private String generateEmailBody(String emailTemplate, String firstName, String lastName, String token, String path, String additionalMessage) {
        StringBuilder emailBody = new StringBuilder();

        String verificationLink = "";
        if (path != null) {
            verificationLink = String.format(url + path, token);
        }

        emailBody.append(String.format(emailTemplate, firstName, lastName, verificationLink));

        if (additionalMessage != null) {
            emailBody.append("\n").append(additionalMessage);
        }

        return emailBody.toString();
    }
}