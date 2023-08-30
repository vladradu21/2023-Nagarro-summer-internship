package com.nagarro.si.pba.repository;

import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.Group;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository {
    Group save(Group group);

    List<Group> findAll();

    Optional<Group> findById(int id);

    void delete(int id);

    void updateBalance(double balance, int groupId);

    int findGroupIdByNameAndUserId(String groupName, int userId);

    Currency getDefaultCurrency(int groupId);
}
