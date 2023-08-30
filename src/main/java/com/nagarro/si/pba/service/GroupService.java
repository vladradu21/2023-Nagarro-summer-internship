package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.GroupDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupService {

    GroupDTO save(GroupDTO groupDTO);

    GroupDTO getById(int id);

    List<GroupDTO> getAll();

    void delete(int id);

    int getGroupIdByNameAndUserId(String groupName, int userId);
}
