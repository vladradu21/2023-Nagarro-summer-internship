package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.ReportRequestDTO;
import com.nagarro.si.pba.service.ReportService;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.utils.TestData;
import com.nagarro.si.pba.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ReportControllerTest extends BaseControllerTest<ReportController> {
    @Mock
    private ReportService reportService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private ReportController reportController;

    @Override
    protected ReportController getController() {
        return reportController;
    }

    @Test
    void generateTransactionReportTest() throws Exception {
        int userId = 1;
        String token = "justATest";

        ReportRequestDTO reportRequestDTO = TestData.returnIncomesReportRequestDTO();

        Path tempFilePath = Files.createTempFile("test", ".xlsx");
        File tempFile = tempFilePath.toFile();
        try {
            when(reportService.generateTransactionReport(reportRequestDTO, userId)).thenReturn(tempFile);
            when(tokenService.extractUserId(token)).thenReturn(userId);

            mockMvc.perform(post("/reports/transactions")
                            .requestAttr("jwtToken", token)
                            .content(TestUtils.asJsonString(reportRequestDTO))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + tempFile.getName()));

            verify(reportService).generateTransactionReport(reportRequestDTO, userId);
            verify(tokenService).extractUserId(token);

        } finally {
            Files.deleteIfExists(tempFilePath);
        }
    }
}
