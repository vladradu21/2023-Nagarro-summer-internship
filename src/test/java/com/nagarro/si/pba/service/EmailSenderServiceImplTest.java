package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.service.impl.EmailSenderServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceImplTest {
    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailSenderServiceImpl emailSenderService;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field mailUsernameField = EmailSenderServiceImpl.class.getDeclaredField("mailUsername");
        Field urlField = EmailSenderServiceImpl.class.getDeclaredField("url");
        Field verifyTokenEndpointField = EmailSenderServiceImpl.class.getDeclaredField("verifyTokenEndpoint");

        mailUsernameField.setAccessible(true);
        urlField.setAccessible(true);
        verifyTokenEndpointField.setAccessible(true);

        mailUsernameField.set(emailSenderService, "pbateam02");
        urlField.set(emailSenderService, "http://localhost:8080");
        verifyTokenEndpointField.set(emailSenderService, "/api/verification?token=%s");
    }

    @Test
    void sendRegistrationEmailTest() {
        UserDTO userDTO = TestData.returnUserDTOForDonJoe();
        ArgumentCaptor<SimpleMailMessage> mailMessageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailSenderService.sendRegistrationEmail(userDTO, "token");

        verify(javaMailSender).send(mailMessageCaptor.capture());
        SimpleMailMessage sentMessage = mailMessageCaptor.getValue();

        assertEquals("pbateam02", sentMessage.getFrom());
        assertEquals(userDTO.email(), Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals("Account verification", sentMessage.getSubject());
        assertTrue(Objects.requireNonNull(sentMessage.getText()).contains("token"));
        assertTrue(sentMessage.getText().contains("http://localhost:8080/api/verification?token=token"));
    }

    @Test
    void sendResetPasswordEmailTest() {
        UserDTO userDTO = TestData.returnUserDTOForDonJoe();
        ArgumentCaptor<SimpleMailMessage> mailMessageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailSenderService.sendResetPasswordEmail(userDTO, "token");

        verify(javaMailSender).send(mailMessageCaptor.capture());
        SimpleMailMessage sentMessage = mailMessageCaptor.getValue();

        assertEquals("pbateam02", sentMessage.getFrom());
        assertEquals(userDTO.email(), Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals("Reset password", sentMessage.getSubject());
        assertTrue(Objects.requireNonNull(sentMessage.getText()).contains("token"));
    }
}
