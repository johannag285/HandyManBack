package com.ias.HandyMan.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ias.HandyMan.model.ServiceReport;
import com.ias.HandyMan.model.ServiceReportRequest;
import com.ias.HandyMan.model.ServiceReportResponse;
import com.ias.HandyMan.repository.database.ServiceReportRepository;

@Repository
public class ReportServiceRepositoryImp implements ReportServiceRepository{
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceRepositoryImp.class);
	private ServiceReportRepository serviceReportRepository; 

	public ReportServiceRepositoryImp(ServiceReportRepository serviceReportRepository) {
		this.serviceReportRepository = serviceReportRepository;
	}
	
	@Override
	public ServiceReportResponse addServiceReport(ServiceReportRequest serviceReportRequest) {
		ServiceReportResponse serviceReportResponse = new ServiceReportResponse();
		try {
			ServiceReport serviceReport = new ServiceReport();
			serviceReport.setEndDateTime(serviceReportRequest.getEndDateTime());
			serviceReport.setIdentificationService(serviceReportRequest.getIdentificationService());
			serviceReport.setIdentificationTechnician(serviceReportRequest.getIdentificationTechnician());
			serviceReport.setStartDateTime(serviceReportRequest.getStartDateTime());
			serviceReport.setExtraTime(serviceReportRequest.isExtraTime());
			serviceReportRepository.save(serviceReport);
			serviceReportResponse.setResponse("Reporte del servicio creado correctamente para: " + serviceReportRequest.getIdentificationService());
		}catch (Exception e) {
			LOGGER.error("error", e.getMessage());
			serviceReportResponse.setResponse(e.getMessage());
		}
		return serviceReportResponse;
	}

}
