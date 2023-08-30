package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.UserService;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VerificationControllerTest extends BaseControllerTest<VerificationController> {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private VerificationController verificationController;

    @Override
    protected VerificationController getController() {
        return verificationController;
    }

    @Test
    void verifyAccountSuccessTest() throws Exception {
        Token token = TestData.returnNewToken();

        when(tokenService.extractUserId(token.getToken())).thenReturn(token.getId());

        mockMvc.perform(get("/verification")
                        .param("token", token.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("Account has been verified successfully."));

        //Verifying that the methods are called exactly once with the provided token
        verify(tokenService).extractUserId(token.getToken());
        verify(userService).setVerifiedAccount(token.getId());
        verify(tokenService).delete(token.getToken());
    }
}