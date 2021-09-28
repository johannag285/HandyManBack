package com.ias.HandyMan.test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.ias.HandyMan.dto.ServiceReportRequestDTO;
import com.ias.HandyMan.dto.ServiceReportResponseDTO;
import com.ias.HandyMan.repository.ReportServiceRepository;
import com.ias.HandyMan.repository.ReportServiceRepositoryImp;
import com.ias.HandyMan.repository.database.ServiceReportRepository;
import com.ias.HandyMan.services.ReportService;
import com.ias.HandyMan.services.ReportServiceImp;

public class ReportServiceTest {
	private ReportService reportService;
	private ReportServiceRepository reportServiceRepository;
	private ServiceReportRepository serviceReportRepository;
	private ModelMapper modelMapper;

	public ReportServiceTest() {
		this.modelMapper = new ModelMapper();
		this.serviceReportRepository = mock(ServiceReportRepository.class);
		this.reportServiceRepository = mock(ReportServiceRepository.class);
		this.reportService = mock(ReportService.class);

		this.reportServiceRepository = new ReportServiceRepositoryImp(serviceReportRepository);
		this.reportService = new ReportServiceImp(reportServiceRepository, modelMapper);
	}

	@Test
	@DisplayName("Test file DTO")
	void testRequest() {
		ServiceReportRequestDTO serviceReportRequestDTO = new ServiceReportRequestDTO();
		serviceReportRequestDTO.setEndDateTime("dsdfdf");
		serviceReportRequestDTO.setIdentificationService("10M");
		serviceReportRequestDTO.setIdentificationTechnician("1012458615");
		serviceReportRequestDTO.setStartDateTime("sdfsf");

		assertAll(() -> {
			assertNotNull(serviceReportRequestDTO);
		}, () -> {
			assertNotNull(serviceReportRequestDTO.getEndDateTime());
		}, () -> {
			assertNotNull(serviceReportRequestDTO.getIdentificationService());
		}, () -> {
			assertNotNull(serviceReportRequestDTO.getIdentificationTechnician());
		}, () -> {
			assertNotNull(serviceReportRequestDTO.getStartDateTime());
		}, () -> {
			assertNotEquals("", serviceReportRequestDTO.getStartDateTime());
		}, () -> {
			assertNotEquals("", serviceReportRequestDTO.getEndDateTime());
		}, () -> {
			assertNotEquals("", serviceReportRequestDTO.getIdentificationService());
		}, () -> {
			assertNotEquals("", serviceReportRequestDTO.getIdentificationTechnician());
		}, () -> {
			assertNotEquals("", serviceReportRequestDTO.getIdentificationTechnician());
		});
	}

	@Test
	void testFormatDates() {
		ServiceReportRequestDTO serviceReportRequestDTO = new ServiceReportRequestDTO();
		serviceReportRequestDTO.setEndDateTime("2021-02-24 07:00:00");
		serviceReportRequestDTO.setIdentificationService("10M");
		serviceReportRequestDTO.setIdentificationTechnician("1012458615");
		serviceReportRequestDTO.setStartDateTime("2021-02-24 20:00:00");
		assertAll(() -> {
			assertTrue(isValidFormat(serviceReportRequestDTO.getEndDateTime()));
		}, () -> {
			assertTrue(isValidFormat(serviceReportRequestDTO.getStartDateTime()));
		}

		);
	}

	@Test
	void testResponseErrorFormatDates() {
		ServiceReportRequestDTO serviceReportRequestDTO = new ServiceReportRequestDTO();
		serviceReportRequestDTO.setEndDateTime("fttt");
		serviceReportRequestDTO.setIdentificationService("10M");
		serviceReportRequestDTO.setIdentificationTechnician("1012458615");
		serviceReportRequestDTO.setStartDateTime("sdfsf");

		ServiceReportResponseDTO serviceReportResponseDTO = new ServiceReportResponseDTO();
		serviceReportResponseDTO = this.reportService.addServiceReport(serviceReportRequestDTO);
		assertTrue(serviceReportResponseDTO.getResponse().startsWith("Unparseable date:"));
	}

	private static boolean isValidFormat(String value) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = sdf.parse(value);
			if (!value.equals(sdf.format(date))) {
				date = null;
			}
		} catch (ParseException ex) {

		}
		return date != null;
	}

	@Test
	void testAddServiceReport() {
		ServiceReportRequestDTO serviceReportRequestDTO = new ServiceReportRequestDTO();
		serviceReportRequestDTO.setEndDateTime("2021-09-23 20:00:00");
		serviceReportRequestDTO.setIdentificationService("50M");
		serviceReportRequestDTO.setIdentificationTechnician("1000831769");
		serviceReportRequestDTO.setStartDateTime("2021-09-23 07:00:00");

		ServiceReportResponseDTO serviceReportResponseDTO = null;
		serviceReportResponseDTO = this.reportService.addServiceReport(serviceReportRequestDTO);
		assertNotNull(serviceReportResponseDTO);
		assertTrue(serviceReportResponseDTO.getResponse().contains("correctamente"));
	}

}
