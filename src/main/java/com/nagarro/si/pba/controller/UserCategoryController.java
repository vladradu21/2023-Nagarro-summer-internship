package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.CategorySettingsDTO;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.UserCategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/user-categories")
public class UserCategoryController {
    private final UserCategoryService userCategoryService;
    private final TokenService tokenService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCategoryController.class);

    @Autowired
    public UserCategoryController(UserCategoryService userCategoryService, TokenService tokenService) {
        this.userCategoryService = userCategoryService;
        this.tokenService = tokenService;
    }

    @GetMapping
    public CategorySettingsDTO getCategories(@RequestAttribute("jwtToken") String token) {
        int userId = tokenService.extractUserId(token);

        LOGGER.info("Getting categories for user with ID: {}", userId);
        return userCategoryService.getCategories(userId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CategorySettingsDTO update(@RequestAttribute("jwtToken") String token, @RequestBody CategorySettingsDTO categorySettingsDTO) {
        int userId = tokenService.extractUserId(token);

        LOGGER.info("Updating categories for user with ID: {}", userId);
        return userCategoryService.update(userId, categorySettingsDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestAttribute("jwtToken") String token, @PathVariable int id) {
        int userId = tokenService.extractUserId(token);

        LOGGER.warn("Deleting category with ID {} for user with ID: {}", id, userId);
        userCategoryService.delete(userId, id);
    }
}