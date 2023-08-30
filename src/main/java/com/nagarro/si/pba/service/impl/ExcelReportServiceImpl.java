package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.model.ExcelWritable;
import com.nagarro.si.pba.service.ExcelReportService;
import com.nagarro.si.pba.utils.ExcelStyling;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
public class ExcelReportServiceImpl implements ExcelReportService {

    @Override
    public void createHeaderRow(XSSFSheet sheet, List<String> headers) {
        XSSFCellStyle headerStyle = ExcelStyling.createHeaderStyle(sheet.getWorkbook());
        Row headerRow = sheet.createRow(0);
        IntStream.range(0, headers.size()).forEach(i ->
                createAndStyleCell(headerRow, i, headers.get(i), headerStyle));
    }

    @Override
    public void writeData(Row row, ExcelWritable data) {
        XSSFWorkbook workbook = (XSSFWorkbook) row.getSheet().getWorkbook();
        List<String> excelRow = data.toExcelRow();
        IntStream.range(0, excelRow.size()).forEach(i ->
                createAndStyleCell(row, i, excelRow.get(i), ExcelStyling.createCenterAlignmentStyle(workbook)));
    }

    @Override
    public XSSFWorkbook createAndPopulateWorkbook(List<? extends ExcelWritable> data, List<String> headers) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("DataSheet");

        createHeaderRow(sheet, headers);

        AtomicInteger rowNum = new AtomicInteger(1);

        data.forEach(excelWritable -> {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            writeData(row, excelWritable);
        });

        return workbook;
    }

    private void createAndStyleCell(Row row, int columnIndex, String value, XSSFCellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
