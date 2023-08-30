package com.nagarro.si.pba.service;

import com.nagarro.si.pba.model.ExcelWritable;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public interface ExcelReportService {
    void createHeaderRow(XSSFSheet sheet, List<String> headers);

    void writeData(Row row, ExcelWritable data);

    XSSFWorkbook createAndPopulateWorkbook(List<? extends ExcelWritable> data, List<String> headers)  throws IOException;
}
