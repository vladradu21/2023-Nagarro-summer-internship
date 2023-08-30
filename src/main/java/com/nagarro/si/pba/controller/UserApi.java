package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface UserApi {
        @Operation(summary = "Get a user by its id", description = "Find a user by its id", tags = "Users")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDTO.class))),
                        @ApiResponse(responseCode = "409", description = "User not found", content = @Content) })
        @GetMapping("/{id}")
        UserDTO getById(@PathVariable int id);

        @Operation(summary = "Get a user by email", description = "Find a user by its email", tags = "Users")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDTO.class))),
                        @ApiResponse(responseCode = "409", description = "User not found", content = @Content) })
        @GetMapping("/email/{email}")
        UserDTO getByEmail(@PathVariable String email);

        @Operation(summary = "Get all users", description = "Get a list of all users", tags = "Users")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of users", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
        @GetMapping
        List<UserDTO> getAll();
}
