package com.nagarro.si.pba.mapper;

import com.nagarro.si.pba.dto.RegisterDTO;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO entityToDTO(User user);

    List<UserDTO> entityToDTO(List<User> users);

    User dtoToEntity(UserDTO userDTO);

    List<User> dtoToEntity(List<UserDTO> usersDTO);

    User dtoToEntity(RegisterDTO registerDTO);
}