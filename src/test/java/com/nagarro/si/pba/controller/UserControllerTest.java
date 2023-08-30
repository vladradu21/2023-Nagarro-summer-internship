package com.nagarro.si.pba.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.service.UserService;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest extends BaseControllerTest<UserController> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    private static String asJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected UserController getController() {
        return userController;
    }

    @Test
    public void testGetUserByID() throws Exception {
        UserDTO mockUserDTO = TestData.returnUserDTOForMichaelStevens();
        when(userService.getById(3)).thenReturn(mockUserDTO);

        mockMvc.perform(get("/users/{id}", 3))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(mockUserDTO)));
    }

    @Test
    public void testGetUserByID_UserNotFound() {
        when(userService.getById(anyInt())).thenThrow(new PbaNotFoundException("User not found"));

        assertThrows(Exception.class, () -> mockMvc.perform(get("/users/{id}", 1)));
    }

    @Test
    public void testGetUserByEmail() throws Exception {
        UserDTO mockUser = TestData.returnUserDTOForDonJoe();
        when(userService.getByEmail("donjoe@gmail.com")).thenReturn(mockUser);

        mockMvc.perform(get("/users//email/{email}", "donjoe@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(mockUser)));
    }

    @Test
    public void testGetUserByEmail_IncorrectEmail() {
        when(userService.getByEmail("incorrect_email@gmail.com")).thenThrow(new PbaNotFoundException("Email not found"));

        assertThrows(Exception.class, () -> mockMvc.perform(get("/users/email/{email}", "incorrect_email@gmail.com")));
    }

    @Test
    public void testUserCredentials() throws Exception {
        UserDTO mockUser = TestData.returnUserDTOForDonJoe();
        when(userService.getById(1)).thenReturn(mockUser);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("donjoe"))
                .andExpect(jsonPath("$.firstName").value("don"))
                .andExpect(jsonPath("$.lastName").value("joe"))
                .andExpect(jsonPath("$.country").value("colombia"))
                .andExpect(jsonPath("$.age").value(38))
                .andExpect(jsonPath("$.email").value("donjoe@gmail.com"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<UserDTO> mockUsers = Arrays.asList(
                TestData.returnUserDTOForDonJoe(),
                TestData.returnUserDTOForBobDumbledore(),
                TestData.returnUserDTOForMichaelStevens());

        when(userService.getAll()).thenReturn(mockUsers);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(mockUsers)));
    }

    @Test
    public void testGetAllUsers_EmptyList() {
        when(userService.getAll()).thenReturn(Collections.emptyList());

        List<UserDTO> result = userController.getAll();

        Assertions.assertEquals(Collections.emptyList(), result);
    }
}