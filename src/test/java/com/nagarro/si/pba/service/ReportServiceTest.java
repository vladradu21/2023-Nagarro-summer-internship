package com.nagarro.si.pba.service;


import com.nagarro.si.pba.dto.ReportRequestDTO;
import com.nagarro.si.pba.exceptions.ExcelCreationException;
import com.nagarro.si.pba.model.Transaction;
import com.nagarro.si.pba.service.impl.ReportServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportServiceImpl reportService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private ExcelReportService excelReportService;

    static Stream<Arguments> reportTestData() {
        return Stream.of(
                Arguments.of(TestData.returnIncomesReportRequestDTO(), Collections.singletonList(TestData.returnRecurringIncomeEntity())),
                Arguments.of(TestData.returnExpensesReportRequestDTO(), Collections.singletonList(TestData.returnRecurringExpenseEntity())),
                Arguments.of(TestData.returnAllTransactionsReportRequestDTO(), Arrays.asList(TestData.returnRecurringIncomeEntity(), TestData.returnRecurringExpenseEntity()))
        );
    }

    @ParameterizedTest
    @MethodSource("reportTestData")
    void testGenerateTransactionReport(ReportRequestDTO reportRequestDTO, List<Transaction> mockTransactions) {
        int userId = 1;

        when(transactionService.getBalancedTransactions(reportRequestDTO.startDate(), reportRequestDTO.endDate(), reportRequestDTO.reportType(), userId))
                .thenReturn(mockTransactions);
        try {
            when(excelReportService.createAndPopulateWorkbook(mockTransactions, TestData.returnTransactionsHeader()))
                    .thenReturn(new XSSFWorkbook());
        } catch (IOException e) {
            fail("Așteptat să nu apară nicio IOException, dar a apărut.");
        }
        File generatedFile = reportService.generateTransactionReport(reportRequestDTO, userId);

        assertNotNull(generatedFile);
        assertTrue(generatedFile.exists());

        verify(transactionService).getBalancedTransactions(reportRequestDTO.startDate(), reportRequestDTO.endDate(), reportRequestDTO.reportType(), userId);
    }

    @Test
    void testGenerateTransactionReport_ThrowsExceptionOnHeaderCreation() {
        ReportRequestDTO reportRequestDTO = TestData.returnIncomesReportRequestDTO();
        int userId = 1;

        List<Transaction> mockTransactions = Collections.singletonList(TestData.returnRecurringIncomeEntity());
        when(transactionService.getBalancedTransactions(reportRequestDTO.startDate(), reportRequestDTO.endDate(), reportRequestDTO.reportType(), userId))
                .thenReturn(mockTransactions);
        try {
            doThrow(new RuntimeException("Workbook creation error"))
                    .when(excelReportService).createAndPopulateWorkbook(mockTransactions, TestData.returnTransactionsHeader());
        } catch (IOException e) {
            fail("Așteptat să nu apară nicio IOException, dar a apărut.");
        }
        assertThrows(RuntimeException.class, () -> reportService.generateTransactionReport(reportRequestDTO, userId));
    }

    @Test
    void testGenerateTransactionReport_ExcelCreationException() throws IOException {
        // given
        ReportRequestDTO reportRequestDTO = TestData.returnIncomesReportRequestDTO();
        int userId = 1;

        List<Transaction> mockTransactions = new ArrayList<>();
        when(transactionService.getBalancedTransactions(any(), any(), any(), anyInt())).thenReturn(mockTransactions);

        // Here, we're making the mock throw an IOException to simulate the failure
        when(excelReportService.createAndPopulateWorkbook(anyList(), anyList()))
                .thenThrow(new IOException("Mocked IOException"));

        // then
        // We then expect the service to throw the ExcelCreationException in response to catching the IOException
        assertThrows(ExcelCreationException.class, () -> reportService.generateTransactionReport(reportRequestDTO, userId));
    }

}
