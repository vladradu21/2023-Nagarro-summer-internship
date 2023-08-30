package com.nagarro.si.pba.service;

import com.nagarro.si.pba.model.ExcelWritable;
import com.nagarro.si.pba.service.impl.ExcelReportServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExcelReportServiceTest {

    private ExcelReportService excelReportService;

    @BeforeEach
    public void setUp() {
        excelReportService = new ExcelReportServiceImpl();
    }

    @Test
    void testCreateAndPopulateWorkbook() {
        List<String> headers = Arrays.asList("Type", "Description", "Amount");
        List<ExcelWritable> data = List.of(TestData.returnRecurringIncomeEntity());
        try {
            XSSFWorkbook workbook = excelReportService.createAndPopulateWorkbook(data, headers);
            XSSFSheet sheet = workbook.getSheet("DataSheet");

            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < headers.size(); i++) {
                assertEquals(headers.get(i), headerRow.getCell(i).getStringCellValue());
            }

            Row dataRow = sheet.getRow(1);
            for (int i = 0; i < data.size(); i++) {
                assertEquals(data.get(i).toExcelRow().get(i), dataRow.getCell(i).getStringCellValue());
            }

        } catch (IOException e) {
            fail("Așteptat să nu apară nicio IOException, dar a apărut.");
        }

    }

}

