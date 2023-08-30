package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    User save(User user);

    List<User> findAll();

    Optional<User> findById(int id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User update(User user);

    void delete(int userId);

    void updateBalance(double balance, int id);

    Currency getDefaultCurrency(int userId);
}