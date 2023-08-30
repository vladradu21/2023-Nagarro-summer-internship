package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.ReportRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public interface ReportApi {
    @Operation(summary = "Generate transaction report",
            description = "Generates a transaction report based on the given criteria and returns it as a file attachment.",
            tags = "Transaction Reports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated transaction report",
                    content = {@Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(implementation = Resource.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request - check the request body",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error - unexpected error during report generation",
                    content = @Content)})
    @PostMapping("/transactions")
    ResponseEntity<Resource> generateTransactionReport(
            @RequestBody ReportRequestDTO reportRequestDTO,
            @RequestAttribute("jwtToken") String token) throws IOException;

}
