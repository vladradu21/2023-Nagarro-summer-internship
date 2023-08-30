package com.nagarro.si.pba.dto;

import com.nagarro.si.pba.model.ReportType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReportRequestDTO(
   LocalDateTime startDate,
   LocalDateTime endDate,
    ReportType reportType) {}


