package com.ias.HandyMan.services;

import com.ias.HandyMan.dto.ServiceReportRequestDTO;
import com.ias.HandyMan.dto.ServiceReportResponseDTO;

public interface ReportService {
      public ServiceReportResponseDTO addServiceReport(ServiceReportRequestDTO serviceReportRequestDTO);
}
