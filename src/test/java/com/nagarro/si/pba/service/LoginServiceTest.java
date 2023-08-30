package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.LoginDTO;
import com.nagarro.si.pba.exceptions.UnauthorizedException;
import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.security.PasswordEncoder;
import com.nagarro.si.pba.service.impl.LoginServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    private LoginService loginService;

    @Mock
    private JWTGenerator jwtGenerator;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    User user = TestData.returnUserForDonJoe();
    LoginDTO loginDTO = TestData.returnLoginDTOForDonJoe();
    LoginDTO loginDTOWithInvalidPassword = TestData.returnLoginDTOForDonJoeWithInvalidPassword();
    User userNotVerified = TestData.returnUserForDonJoeNotVerifiedAndHashed();
    String jwt = "test.jwt.token";

    @BeforeEach
    public void setup() {
        loginService = new LoginServiceImpl(jwtGenerator, userRepository, tokenService, passwordEncoder);
        user.setPassword(passwordEncoder.hashPassword(user.getPassword()));
    }


    @Test
    public void testLoginWithValidCredentials() {
        user.setIsVerified(true);

        Mockito.lenient().when(passwordEncoder.hashPassword(user.getPassword())).thenReturn("donjoe123");
        Mockito.lenient().when(passwordEncoder.checkPasswordMatch("donjoe123", user.getPassword())).thenReturn(true);
        when(userRepository.findByUsername(loginDTO.username())).thenReturn(Optional.of(user));
        when(jwtGenerator.generateJwtForLogin(user.getId(), loginDTO.username())).thenReturn(jwt);
        Token token = new Token(jwt);
        when(tokenService.save(token)).thenReturn(token);
        when(jwtGenerator.generateTokenObject(jwt)).thenReturn(token);

        String loginResponse = loginService.login(loginDTO);

        assertNotNull(loginResponse);
        assertEquals(jwt, loginResponse);
    }

    @Test
    public void testLoginWithNonExistentUsername() {
        when(userRepository.findByUsername(loginDTO.username())).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> loginService.login(loginDTO));
    }

    @Test
    public void testLoginWithInvalidPassword() {
        when(userRepository.findByUsername(loginDTO.username())).thenReturn(Optional.ofNullable(user));
        assertThrows(UnauthorizedException.class, () -> loginService.login(loginDTOWithInvalidPassword));
    }

    @Test
    public void testLoginWhenUserNotVerified() {
        when(userRepository.findByUsername(loginDTO.username())).thenReturn(Optional.ofNullable(userNotVerified));
        assertThrows(UnauthorizedException.class, () -> loginService.login(loginDTO));
    }
}