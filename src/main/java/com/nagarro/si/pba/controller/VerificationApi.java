package com.nagarro.si.pba.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface VerificationApi {
    @Operation(summary = "Verify user account",
            description = "Verifies a user's account using a provided token",
            tags = "User Verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully verified the user's account",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = " Token not valid or already used",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Token expired",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @GetMapping()
    ResponseEntity<String> verifyAccount(@RequestParam("token") String token);
}
