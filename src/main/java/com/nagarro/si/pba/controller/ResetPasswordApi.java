package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.PasswordDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


public interface ResetPasswordApi {
    @Operation(summary = "Trigger password reset", description = "Generate and send password reset token", tags = "Reset Password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset token sent",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Could not send password reset token - user not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error - invalid JSON",
                    content = @Content)})

    @PostMapping("/reset-password")
    void triggerResetPassword(@RequestBody String emailJson);

    @Operation(summary = "Reset password", description = "Reset user password using a token", tags = "Reset Password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request - verification token expired",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Could not reset password - user not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error - verification token not found or already used",
                    content = @Content)})

    @PutMapping("/update-password")
    String resetPassword(@RequestAttribute("jwtToken") String token, @RequestBody @Valid PasswordDTO passwordDTO);
}
