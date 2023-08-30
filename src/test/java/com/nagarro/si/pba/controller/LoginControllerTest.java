package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.LoginDTO;
import com.nagarro.si.pba.service.LoginService;
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
class LoginControllerTest extends BaseControllerTest<LoginController> {
    @Mock
    private LoginService loginService;
    @InjectMocks
    private LoginController loginController;

    private final LoginDTO loginDTO = TestData.returnLoginDTOForDonJoe();
    private final String token = TestData.returnTokenFromLoginForDonJoe();

    @Override
    protected LoginController getController() {
        return loginController;
    }

    @Test
    void loginTest() throws Exception {
        when(loginService.login(loginDTO)).thenReturn(token);

        mockMvc.perform(post("/login")
                        .content(TestUtils.asJsonString(loginDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(loginService).login(loginDTO);
    }
}
