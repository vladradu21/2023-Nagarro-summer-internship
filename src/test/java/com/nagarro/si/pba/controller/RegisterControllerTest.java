package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.RegisterDTO;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.service.EmailSenderService;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.UserService;
import com.nagarro.si.pba.utils.TestData;
import com.nagarro.si.pba.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest extends BaseControllerTest<RegisterController> {
    @Mock
    private UserService userService;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private JWTGenerator jwtGenerator;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private RegisterController registerController;

    @Override
    protected RegisterController getController() {
        return registerController;
    }

    @Test
    void create_RegisterDtoGiven_WhenHttpStatusCreated() throws Exception {
        RegisterDTO registerDto = TestData.returnRegisterDTOForDonJoe();
        UserDTO userDTO = TestData.returnUserDTOForDonJoe();
        Token token = TestData.returnNewToken();

        when(userService.save(registerDto)).thenReturn(userDTO);
        when(jwtGenerator.generateJwtForRegistration(1, "donjoe")).thenReturn("newToken");
        when(jwtGenerator.generateTokenObject("newToken")).thenReturn(token);

        mockMvc.perform(post("/register")
                        .content(TestUtils.asJsonString(registerDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(userService).save(registerDto);
        verify(emailSenderService).sendRegistrationEmail(userDTO, "newToken");
        verify(tokenService).save(token);
    }
}