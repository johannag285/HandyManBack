package com.ias.HandyMan.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ias.HandyMan.dto.ServiceReportRequestDTO;
import com.ias.HandyMan.dto.ServiceReportResponseDTO;
import com.ias.HandyMan.model.ServiceReportRequest;
import com.ias.HandyMan.model.ServiceReportResponse;
import com.ias.HandyMan.repository.ReportServiceRepository;

@Service
public class ReportServiceImp implements ReportService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportService.class);
    private ReportServiceRepository reportServiceRepository;
    private ModelMapper modelMapper;
    
    @Autowired
	public ReportServiceImp(ReportServiceRepository reportServiceRepository, ModelMapper modelMapper) {
    	this.reportServiceRepository = reportServiceRepository;
		this.modelMapper = modelMapper;
	}
	
	@Override
	public ServiceReportResponseDTO addServiceReport(ServiceReportRequestDTO serviceReportRequestDTO) {
		ServiceReportResponseDTO serviceReportResponseDTO = new ServiceReportResponseDTO();
		if (serviceReportRequestDTO.getIdentificationService().isEmpty()
				|| serviceReportRequestDTO.getIdentificationService() == null) {
			serviceReportResponseDTO.setResponse("Debe ingresar la identificación del servicio");
		} else if (serviceReportRequestDTO.getIdentificationTechnician().isEmpty()
				|| serviceReportRequestDTO.getIdentificationTechnician() == null) {
			serviceReportResponseDTO.setResponse("Debe ingresar la identificación del técnico");
		} else if (serviceReportRequestDTO.getStartDateTime().isEmpty()
				|| serviceReportRequestDTO.getStartDateTime() == null) {
			serviceReportResponseDTO.setResponse("Debe ingresar la fecha de inicio");
		} else if (serviceReportRequestDTO.getEndDateTime().isEmpty()
				|| serviceReportRequestDTO.getEndDateTime() == null) {
			serviceReportResponseDTO.setResponse("Debe ingresar la fecha de fin");
		} else {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			try {
				Date dateStart = formatter.parse(serviceReportRequestDTO.getStartDateTime());
				Date dateEnd = formatter.parse(serviceReportRequestDTO.getEndDateTime());
				
				int resultDate = dateStart.compareTo(dateEnd);
				if (resultDate > 0) {
					serviceReportResponseDTO.setResponse("La fecha de de inicio debe ser menor a la fecha de fin");
				}else {
					ServiceReportRequest serviceReportRequest = new ServiceReportRequest();
					serviceReportRequest.setEndDateTime(dateEnd);
					serviceReportRequest.setStartDateTime(dateStart);
					serviceReportRequest.setIdentificationTechnician(serviceReportRequestDTO.getIdentificationTechnician());
					serviceReportRequest.setIdentificationService(serviceReportRequestDTO.getIdentificationService());
					ServiceReportResponse serviceReportResponse = this.reportServiceRepository.addServiceReport(serviceReportRequest);
					serviceReportResponseDTO = modelMapper.map(serviceReportResponse, ServiceReportResponseDTO.class);
				}
			} catch (ParseException e) {
				LOGGER.error("error", e.getMessage());
				serviceReportResponseDTO.setResponse(e.getMessage());
			}
		}
		return serviceReportResponseDTO;
	}

}
