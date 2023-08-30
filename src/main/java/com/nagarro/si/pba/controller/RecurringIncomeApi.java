package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.IncomeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

public interface RecurringIncomeApi {
    @Operation(summary = "Add Recurring Income",
            description = "Add a new recurring income for a user identified by the Authorization token",
            tags = "Income Management")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Recurring income added successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = IncomeDTO.class))}),
            @ApiResponse(responseCode = "409",
                    description = "Invalid recurring income - repetition flow is 'NONE'",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - invalid or missing Authorization token",
                    content = @Content),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content)})
    @PostMapping("/recurring")
    IncomeDTO addIncome(@RequestAttribute("jwtToken") String token, @Valid @RequestBody IncomeDTO incomeDTO);
}