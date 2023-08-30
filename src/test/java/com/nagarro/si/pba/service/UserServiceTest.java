package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.RegisterDTO;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.mapper.UserMapper;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.security.PasswordEncoder;
import com.nagarro.si.pba.service.impl.UserServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder);
    }

    @Test
    void testSave() {
        RegisterDTO registerDTO = TestData.returnRegisterDTOForDonJoe();
        User user = new User();
        user.setEmail(registerDTO.email());
        when(userMapper.dtoToEntity(registerDTO)).thenReturn(user);

        UserDTO userDTO = new UserDTO(null, registerDTO.email(), registerDTO.username(), registerDTO.defaultCurrency(),
                registerDTO.firstName(), registerDTO.lastName(), registerDTO.country(), registerDTO.age());
        when(userMapper.entityToDTO(user)).thenReturn(userDTO);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        UserDTO resultDTO = userService.save(registerDTO);

        Assertions.assertEquals(resultDTO.email(), registerDTO.email());
    }

    @Test
    void testGetAll() {
        List<User> userList = new ArrayList<>();
        userList.add(TestData.returnUserForBobDumbledore());
        userList.add(TestData.returnUserForMichaelStevens());

        when(userRepository.findAll()).thenReturn(userList);

        List<UserDTO> expectedUserDTOList = new ArrayList<>();
        expectedUserDTOList.add(TestData.returnUserDTOForBobDumbledore());
        expectedUserDTOList.add(TestData.returnUserDTOForMichaelStevens());

        when(userMapper.entityToDTO(userList)).thenReturn(expectedUserDTOList);

        List<UserDTO> resultDTOList = userService.getAll();

        Assertions.assertEquals(expectedUserDTOList, resultDTOList);
    }

    @Test
    void testGetUserById_HappyPath() {
        User user = new User();
        int expectedId = 1;
        user.setId(expectedId);
        when(userRepository.findById(expectedId)).thenReturn(Optional.of(user));

        UserDTO userDTO = TestData.returnUserDTOForDonJoe();
        when(userMapper.entityToDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getById(expectedId);

        Assertions.assertEquals(userDTO, result);
        Assertions.assertEquals(expectedId, result.id());
    }

    @Test
    void testGetUserById_WrongId() {
        int wrongId = 2;
        when(userRepository.findById(wrongId)).thenReturn(Optional.empty());

        assertThrows(PbaNotFoundException.class, () -> userService.getById(wrongId));
    }

    @Test
    void testGetUserByEmail_HappyPath() {
        User user = new User();
        String expectedEmail = "donjoe@gmail.com";
        user.setEmail(expectedEmail);
        when(userRepository.findByEmail(expectedEmail)).thenReturn(Optional.of(user));

        UserDTO userDTO = TestData.returnUserDTOForDonJoe();
        when(userMapper.entityToDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getByEmail(expectedEmail);

        Assertions.assertEquals(userDTO, result);
        Assertions.assertEquals(expectedEmail, result.email());
    }

    @Test
    void testGetUserByEmail_WrongEmail() {
        String wrongEmail = "wrong@gmail.com";
        when(userRepository.findByEmail(wrongEmail)).thenReturn(Optional.empty());

        assertThrows(PbaNotFoundException.class, () -> userService.getByEmail(wrongEmail));
    }

    @Test
    void testGetUserByUsername_HappyPath() {
        User user = new User();
        String expectedUsername = "michaelstevens";
        user.setUsername(expectedUsername);
        when(userRepository.findByUsername(expectedUsername)).thenReturn(Optional.of(user));

        UserDTO userDTO = TestData.returnUserDTOForMichaelStevens();
        when(userMapper.entityToDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getByUsername(expectedUsername);

        Assertions.assertEquals(userDTO, result);
        Assertions.assertEquals(expectedUsername, result.username());
    }

    @Test
    void testGetUserByUsername_WrongUsername() {
        String wrongUsername = "wrong";
        when(userRepository.findByUsername(wrongUsername)).thenReturn(Optional.empty());

        assertThrows(PbaNotFoundException.class, () -> userService.getByUsername(wrongUsername));
    }
}