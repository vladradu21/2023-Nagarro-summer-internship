package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.LoginDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface LoginApi {
    @Operation(summary = "Login", description = "Login existing, verified users and generating a JWT token", tags = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login Successful",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - token expired",
                    content = @Content)})


    @PostMapping(path = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginDTO loginDTO);
}
