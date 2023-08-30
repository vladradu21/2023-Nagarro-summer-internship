package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.ReportRequestDTO;

import java.io.File;

public interface ReportService {
    File generateTransactionReport(ReportRequestDTO reportRequestDTO, int userId);
}
