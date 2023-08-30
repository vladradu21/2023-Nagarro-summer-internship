package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.dto.ReportRequestDTO;
import com.nagarro.si.pba.exceptions.ExcelCreationException;
import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.model.Transaction;
import com.nagarro.si.pba.service.ExcelReportService;
import com.nagarro.si.pba.service.ReportService;
import com.nagarro.si.pba.service.TransactionService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final TransactionService transactionService;
    private final ExcelReportService excelReportService;
    private final List<String> transactionsHeader = Arrays.asList("Transaction Type", "Category", "Name", "Amount", "Description", "Currency", "Added Date", "Balance After Transaction");

    @Autowired
    public ReportServiceImpl(TransactionService transactionService, ExcelReportService excelReportService) {
        this.transactionService = transactionService;
        this.excelReportService = excelReportService;

    }

    @Override
    public File generateTransactionReport(ReportRequestDTO reportRequestDTO, int userId) {

        List<Transaction> transactions = transactionService.getBalancedTransactions(reportRequestDTO.startDate(), reportRequestDTO.endDate(), reportRequestDTO.reportType(), userId);

        String filename = generateTransactionReportName(reportRequestDTO, userId);
        File tempFile = new File(System.getProperty("java.io.tmpdir"), filename);

        try (XSSFWorkbook workbook = excelReportService.createAndPopulateWorkbook(transactions, transactionsHeader); FileOutputStream out = new FileOutputStream(tempFile)) {
            workbook.write(out);
            return tempFile;
        } catch (IOException e) {
            throw new ExcelCreationException(ExceptionMessage.ERROR_CREATING_EXCEL_FILE.format());
        }
    }

    private String generateTransactionReportName(ReportRequestDTO reportRequestDTO, int userId) {
        StringBuilder filenameBuilder = new StringBuilder();

        filenameBuilder.append("export_user_")
                .append(userId)
                .append("_")
                .append(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(reportRequestDTO.startDate()))
                .append("_")
                .append(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(reportRequestDTO.endDate()))
                .append(".xlsx");

        return filenameBuilder.toString();
    }
}
