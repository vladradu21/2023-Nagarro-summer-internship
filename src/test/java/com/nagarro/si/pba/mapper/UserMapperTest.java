package com.nagarro.si.pba.mapper;


import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testEntityToDto() {
        User user = TestData.returnUserForDonJoe();

        UserDTO userDTO = userMapper.entityToDTO(user);

        assertEquals(user.getEmail(), userDTO.email());
        assertEquals(user.getUsername(), userDTO.username());
        assertEquals(user.getFirstName(), userDTO.firstName());
        assertEquals(user.getLastName(), userDTO.lastName());
        assertEquals(user.getCountry(), userDTO.country());
        assertEquals(user.getAge(), userDTO.age());
    }

    @Test
    void testEntityListToDtoList() {
        List<User> users = TestData.returnListOfUsers();

        List<UserDTO> userDTOS = userMapper.entityToDTO(users);

        assertEquals(users.size(), userDTOS.size());
        for (int i = 0; i < users.size(); i++) {
            assertEquals(users.get(i).getEmail(), userDTOS.get(i).email());
            assertEquals(users.get(i).getUsername(), userDTOS.get(i).username());
            assertEquals(users.get(i).getFirstName(), userDTOS.get(i).firstName());
            assertEquals(users.get(i).getLastName(), userDTOS.get(i).lastName());
            assertEquals(users.get(i).getCountry(), userDTOS.get(i).country());
            assertEquals(users.get(i).getAge(), userDTOS.get(i).age());
        }
    }

    @Test
    void testDtoToEntity() {
        UserDTO userDTO = TestData.returnUserDTOForDonJoe();

        User user = userMapper.dtoToEntity(userDTO);

        assertEquals(userDTO.email(), user.getEmail());
        assertEquals(userDTO.username(), user.getUsername());
        assertEquals(userDTO.firstName(), user.getFirstName());
        assertEquals(userDTO.lastName(), user.getLastName());
        assertEquals(userDTO.country(), user.getCountry());
        assertEquals(userDTO.age(), user.getAge());
    }

    @Test
    void testDtoListToEntityList() {
        List<UserDTO> userDTOS = TestData.returnListOfUserDtos();

        List<User> users = userMapper.dtoToEntity(userDTOS);

        assertEquals(userDTOS.size(), users.size());
        for (int i = 0; i < userDTOS.size(); i++) {
            assertEquals(userDTOS.get(i).email(), users.get(i).getEmail());
            assertEquals(userDTOS.get(i).username(), users.get(i).getUsername());
            assertEquals(userDTOS.get(i).firstName(), users.get(i).getFirstName());
            assertEquals(userDTOS.get(i).lastName(), users.get(i).getLastName());
            assertEquals(userDTOS.get(i).country(), users.get(i).getCountry());
            assertEquals(userDTOS.get(i).age(), users.get(i).getAge());
        }
    }
}
