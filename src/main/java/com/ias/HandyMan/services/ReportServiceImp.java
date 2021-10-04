package com.ias.HandyMan.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ias.HandyMan.dto.ServiceReportRequestDTO;
import com.ias.HandyMan.dto.ServiceReportResponseDTO;
import com.ias.HandyMan.model.CalculateWorkingHourRequest;
import com.ias.HandyMan.model.ServiceReport;
import com.ias.HandyMan.model.ServiceReportRequest;
import com.ias.HandyMan.model.ServiceReportResponse;
import com.ias.HandyMan.repository.CalculateWorkingHourRepository;
import com.ias.HandyMan.repository.ReportServiceRepository;

@Service
public class ReportServiceImp implements ReportService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportService.class);
    private ReportServiceRepository reportServiceRepository;
    private CalculateWorkingHourRepository calculateWorkingHourRepository;
    private ModelMapper modelMapper;
    private long testUnit;
    
    @Autowired
	public ReportServiceImp(ReportServiceRepository reportServiceRepository, ModelMapper modelMapper, CalculateWorkingHourRepository calculateWorkingHourRepository) {
    	this.reportServiceRepository = reportServiceRepository;
    	this.calculateWorkingHourRepository = calculateWorkingHourRepository;
		this.modelMapper = modelMapper;
		this.testUnit = 0;
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
					boolean isExtraTime = consultHoursWeek(dateStart,  serviceReportRequestDTO.getIdentificationTechnician());
					ServiceReportRequest serviceReportRequest = new ServiceReportRequest();
					serviceReportRequest.setEndDateTime(dateEnd);
					serviceReportRequest.setStartDateTime(dateStart);
					serviceReportRequest.setIdentificationTechnician(serviceReportRequestDTO.getIdentificationTechnician());
					serviceReportRequest.setIdentificationService(serviceReportRequestDTO.getIdentificationService());
					serviceReportRequest.setExtraTime(isExtraTime);
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
	
	private boolean consultHoursWeek(Date dateStartRegistry, String identificationTechnician) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateStartRegistry);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		String startDateTime = sdf.format(calendar.getTime());
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		String endDateTime = sdf.format(calendar.getTime());
		
		Date dateStart = null;
		Date dateEnd = null;
		try {
			dateStart = formatterDate.parse(startDateTime);
			dateEnd = formatterDate.parse(endDateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		CalculateWorkingHourRequest calculateWorkingHourRequest = new CalculateWorkingHourRequest();
		calculateWorkingHourRequest.setEndDateTime(dateEnd);
		calculateWorkingHourRequest.setStartDateTime(dateStart);
		calculateWorkingHourRequest.setIdentificationTechnician(identificationTechnician);
		calculateWorkingHourRequest.setEndTime(dateEnd);
		calculateWorkingHourRequest.setStartTime(dateStart);
		
		List<ServiceReport> listServiceReport = calculateWorkingHourRepository.getQueryWeek(calculateWorkingHourRequest).get();
		String time = calculateHours(listServiceReport);
		String splitHour[] = time.split(":");
		if (Integer.valueOf(splitHour[0]) >= 48) {
			return true;
		}
		return false;
	}
	
	private String calculateHours(List<ServiceReport> listServiceReport) {
		testUnit = 0;
		listServiceReport.forEach(reportService -> {
			Calendar calendarStartTime = Calendar.getInstance();
			calendarStartTime.setTime(reportService.getStartDateTime());

			Calendar calendarEndTime = Calendar.getInstance();
			calendarEndTime.setTime(reportService.getEndDateTime());
			
			LocalDateTime fromDateTime = null;
			LocalDateTime toDateTime = null;
			
			fromDateTime = calculateLocalDateTime(calendarStartTime);
			toDateTime = calculateLocalDateTime(calendarEndTime);

			Duration duration = Duration.between(fromDateTime, toDateTime);
			testUnit = testUnit + duration.toMillis();
		});
		
		long millis = testUnit;
		String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		String totalnormalHours = hms;
		testUnit = 0;
		return totalnormalHours;
	}
	
	private LocalDateTime calculateLocalDateTime(Calendar calendar) {
		LocalDateTime localDateTime = LocalDateTime.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND));
		return localDateTime;
	}
}
