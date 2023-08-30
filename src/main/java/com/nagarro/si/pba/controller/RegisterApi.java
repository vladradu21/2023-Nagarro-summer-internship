package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.RegisterDTO;
import com.nagarro.si.pba.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface RegisterApi {
    @Operation(summary = "Create new user",
            description = "Registers a new user and sends an email with a verification link",
            tags = "User Registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered new user",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request - check the request body",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error - invalid JSON or server error",
                    content = @Content)})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    UserDTO create(@RequestBody RegisterDTO registerDTO);
}
