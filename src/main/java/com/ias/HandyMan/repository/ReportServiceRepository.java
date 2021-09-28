package com.ias.HandyMan.repository;

import com.ias.HandyMan.model.ServiceReportRequest;
import com.ias.HandyMan.model.ServiceReportResponse;


public interface ReportServiceRepository {
	public ServiceReportResponse addServiceReport(ServiceReportRequest serviceReportRequest);
}
