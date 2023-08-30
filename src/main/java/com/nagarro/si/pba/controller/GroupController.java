package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.GroupDTO;
import com.nagarro.si.pba.dto.RoleDTO;
import com.nagarro.si.pba.model.RoleType;
import com.nagarro.si.pba.service.GroupService;
import com.nagarro.si.pba.service.RoleService;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.UserGroupRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController implements GroupApi {
    private final GroupService groupService;

    private final TokenService tokenService;

    private final UserGroupRoleService userGroupRoleService;

    private final RoleService roleService;

    @Autowired
    public GroupController(GroupService groupService, TokenService tokenService, UserGroupRoleService userGroupRoleService, RoleService roleService) {
        this.groupService = groupService;
        this.tokenService = tokenService;
        this.userGroupRoleService = userGroupRoleService;
        this.roleService = roleService;
    }

    @Override
    public GroupDTO create(String token, GroupDTO groupDTO) {
        int userId = tokenService.extractUserId(token);
        GroupDTO savedGroupDTO =  groupService.save(groupDTO);
        RoleDTO roleDTO = roleService.getByType(RoleType.ADMIN);
        userGroupRoleService.assignUserGroupRole(userId, savedGroupDTO.id(), roleDTO.id());
        return savedGroupDTO;
    }

    @Override
    public GroupDTO getById(int id) {
        return groupService.getById(id);
    }

    @Override
    public List<GroupDTO> getAll() {
        return groupService.getAll();
    }

    @Override
    public void delete(int id) {
        userGroupRoleService.delete(id);
        groupService.delete(id);
    }

    @Override
    public void assignUserToGroup(int groupId, String token) {
        int userId = tokenService.extractUserId(token);
        RoleDTO roleDTO = roleService.getByType(RoleType.ADMIN);

        userGroupRoleService.assignUserGroupRole(userId, groupId, roleDTO.id());
    }

    @Override
    public void removeUserFromGroup(String name, String token) {
        int userId = tokenService.extractUserId(token);
        int groupId = groupService.getGroupIdByNameAndUserId(name, userId);

        userGroupRoleService.removeUserFromGroup(groupId, userId);
    }
}