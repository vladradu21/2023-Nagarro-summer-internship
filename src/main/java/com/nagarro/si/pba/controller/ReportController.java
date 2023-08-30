package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.ReportRequestDTO;
import com.nagarro.si.pba.service.ReportService;
import com.nagarro.si.pba.service.TokenService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/reports")
public class ReportController implements ReportApi {

    private final ReportService reportService;
    private final TokenService tokenService;

    public ReportController(ReportService reportService, TokenService tokenService) {
        this.reportService = reportService;
        this.tokenService = tokenService;
    }

    @Override
    public ResponseEntity<Resource> generateTransactionReport(ReportRequestDTO reportRequestDTO, String token) throws IOException {
        File report = reportService.generateTransactionReport(reportRequestDTO, tokenService.extractUserId(token));

        Path path = Paths.get(report.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(report.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
