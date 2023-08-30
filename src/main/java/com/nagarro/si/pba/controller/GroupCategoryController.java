package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.CategorySettingsDTO;
import com.nagarro.si.pba.service.GroupCategoryService;
import com.nagarro.si.pba.service.GroupService;
import com.nagarro.si.pba.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group-categories/{groupName}")
public class GroupCategoryController {
    private final GroupCategoryService groupCategoryService;
    private final TokenService tokenService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupCategoryController.class);

    private final GroupService groupService;

    public GroupCategoryController(GroupCategoryService groupCategoryService, TokenService tokenService, GroupService groupService) {
        this.groupCategoryService = groupCategoryService;
        this.tokenService = tokenService;
        this.groupService = groupService;
    }

    @GetMapping
    public CategorySettingsDTO getCategories(@PathVariable String groupName, @RequestAttribute("jwtToken") String token) {
        int userId = tokenService.extractUserId(token);
        int groupId = groupService.getGroupIdByNameAndUserId(groupName, userId);

        LOGGER.info("Getting categories for user with ID: {} and group name: {}", userId, groupName);
        return groupCategoryService.getCategories(groupId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CategorySettingsDTO update(@PathVariable String groupName, @RequestAttribute("jwtToken") String token, @RequestBody CategorySettingsDTO categorySettingsDTO) {
        int userId = tokenService.extractUserId(token);
        int groupId = groupService.getGroupIdByNameAndUserId(groupName, userId);

        LOGGER.info("Updating categories for user with ID: {} and group name: {}", userId, groupName);
        return groupCategoryService.update(groupId, categorySettingsDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable String groupName, @RequestAttribute("jwtToken") String token, @PathVariable int id) {
        int userId = tokenService.extractUserId(token);
        int groupId = groupService.getGroupIdByNameAndUserId(groupName, userId);

        LOGGER.warn("Deleting category with ID {} for user with ID: {} and group name: {}", id, userId, groupName);
        groupCategoryService.delete(groupId, id);
    }
}