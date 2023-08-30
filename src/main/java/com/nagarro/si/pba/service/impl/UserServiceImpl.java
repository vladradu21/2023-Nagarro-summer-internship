package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.dto.RegisterDTO;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.PbaConflictException;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.mapper.UserMapper;
import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.security.PasswordEncoder;
import com.nagarro.si.pba.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO save(RegisterDTO registerDTO) {
        if (userRepository.findByEmail(registerDTO.email()).isPresent()) {
            LOGGER.warn("Attempting to save user with duplicate email: {}", registerDTO.email());
            throw new PbaConflictException(ExceptionMessage.DUPLICATE_EMAIL.format(registerDTO.email()));
        }
        User userToSave = userMapper.dtoToEntity(registerDTO);
        userToSave.setPassword(passwordEncoder.hashPassword(userToSave.getPassword()));
        User savedUser = userRepository.save(userToSave);

        LOGGER.info("User saved successfully: {}", savedUser.getUsername());
        return userMapper.entityToDTO(savedUser);
    }

    @Override
    public List<UserDTO> getAll() {
        List<User> users = userRepository.findAll();
        return userMapper.entityToDTO(users);
    }

    @Override
    public UserDTO getById(int id) {
        return userRepository.findById(id)
                .map(userMapper::entityToDTO)
                .orElseThrow(() -> {
                    LOGGER.warn("User not found with ID: {}", id);
                    return new PbaNotFoundException(ExceptionMessage.USER_NOT_FOUND.format(id));
                });
    }

    @Override
    public UserDTO getByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::entityToDTO)
                .orElseThrow(() -> {
                    LOGGER.warn("User not found with username: {}", username);
                    return new PbaNotFoundException(ExceptionMessage.USER_NOT_FOUND.format(username));
                });
    }

    @Override
    public UserDTO getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::entityToDTO)
                .orElseThrow(() -> {
                    LOGGER.warn("User not found with email: {}", email);
                    return new PbaNotFoundException(ExceptionMessage.EMAIL_NOT_FOUND.format(email));
                });
    }

    @Override
    public void setVerifiedAccount(int id) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new PbaNotFoundException(ExceptionMessage.USER_NOT_FOUND.format(id)));
        if (existingUser.getIsVerified()) {
            throw new PbaNotFoundException(ExceptionMessage.VERIFICATION_TOKEN_NOT_FOUND_OR_ALREADY_USED.format());
        } else {
            existingUser.setIsVerified(true);
        }

        userRepository.update(existingUser);
        LOGGER.info("User with ID {} has been verified.", id);
    }

    @Override
    public void delete(int userId) {
        userRepository.delete(userId);
        LOGGER.info("User with ID {} has been deleted.", userId);
    }

    @Override
    public Currency getDefaultCurrency(int userId){
        return userRepository.getDefaultCurrency(userId);
    }

    @Override
    public void updatePassword(int userId, String newPassword) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setPassword(passwordEncoder.hashPassword(newPassword));
            userRepository.update(user);
            LOGGER.info("Password updated for user with ID: {}", userId);
        });
    }
}