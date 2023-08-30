package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.TransactionDTO;
import com.nagarro.si.pba.dto.TransactionFilterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface TransactionControllerApi {
    @Operation(summary = "Get all transactions", description = "Get all transactions for current user by userId from token", tags = "transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of transactions",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionFilterDTO.class)))})
    @GetMapping
    List<TransactionDTO> getAll(
            @RequestBody @Valid TransactionFilterDTO transactionFilterDTO,
            @RequestAttribute("jwtToken") String token
    );
    @Operation(summary = "Add a new transaction", description = "Add a new transaction for the current user using the userId from token", tags = "transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction added successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, invalid or missing JWT token")
    })

    @PostMapping("/add")
    void addTransaction(
            @RequestAttribute("jwtToken") String token,
            @RequestBody @Valid TransactionDTO transactionDTO
    );
}
