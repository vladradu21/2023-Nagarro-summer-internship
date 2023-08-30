package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.service.EmailSenderService;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.UserService;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ResetPasswordControllerTest extends BaseControllerTest<ResetPasswordController> {
    @Mock
    private TokenService tokenService;
    @Mock
    private UserService userService;
    @Mock
    private JWTGenerator jwtGenerator;
    @InjectMocks
    private ResetPasswordController resetPasswordController;

    @Mock
    private EmailSenderService emailSenderService;

    @Override
    protected ResetPasswordController getController() {
        return resetPasswordController;
    }

    @Test
    public void testTriggerResetPassword() throws Exception {
        String emailJson = "{\"email\": \"donjoe@gmail.com\"}";
        UserDTO user = TestData.returnUserDTOForDonJoe();

        when(userService.getByEmail("donjoe@gmail.com")).thenReturn(user);
        when(jwtGenerator.generateJwtForRegistration(user.id(), user.username())).thenReturn(TestData.returnValidToken().getToken());
        when(jwtGenerator.generateTokenObject(TestData.returnValidToken().getToken())).thenReturn(TestData.returnValidToken());
        doNothing().when(emailSenderService).sendResetPasswordEmail(user, TestData.returnValidToken().getToken());

        mockMvc.perform(post("/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testResetPassword() throws Exception {
        String token = "validToken";
        int expectedUserId = 1;
        String newPassword = "newPassword";


        when(tokenService.extractUserId(token)).thenReturn(expectedUserId);
        doNothing().when(userService).updatePassword(expectedUserId, newPassword);
        doNothing().when(tokenService).delete(token);

        mockMvc.perform(put("/update-password")
                        .requestAttr("jwtToken", "validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\": \"" + newPassword + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password has been reset successfully."));

        verify(tokenService).findToken(token);
        verify(tokenService).delete(token);
        verify(userService).updatePassword(expectedUserId, newPassword);

    }
}