package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.RegisterDTO;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.model.Currency;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserDTO save(RegisterDTO registerDTO);

    UserDTO getById(int id);

    UserDTO getByUsername(String username);

    UserDTO getByEmail(String email);

    List<UserDTO> getAll();

    void setVerifiedAccount(int id);

    void delete(int userId);

    Currency getDefaultCurrency(int userId);

    /**
     * Changes the password of the specified user to the new password provided.
     *
     * @param userId      The ID of the user whose password is to be changed.
     * @param newPassword The new password to be set for the user.
     * @throws PbaNotFoundException if no user with the provided userId exists.
     */
    void updatePassword(int userId, String newPassword);
}
