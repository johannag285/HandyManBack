package com.ias.HandyMan.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import com.ias.HandyMan.dto.CalculateWorkingHourResponseDTO;
import com.ias.HandyMan.repository.CalculateWorkingHourRepository;
import com.ias.HandyMan.repository.CalculateWorkingHourRepositoryImp;
import com.ias.HandyMan.repository.database.ServiceReportRepository;
import com.ias.HandyMan.services.CalculateWorkingHourService;
import com.ias.HandyMan.services.CalculateWorkingHourServiceImp;

public class CalculateWorkingHourServiceTest {
	private CalculateWorkingHourService calculateWorkingHourService;
	private CalculateWorkingHourRepository calculateWorkingHourRepository;
	private ServiceReportRepository serviceReportRepository;

	public CalculateWorkingHourServiceTest() {
		calculateWorkingHourService = mock(CalculateWorkingHourService.class);
		calculateWorkingHourRepository = mock(CalculateWorkingHourRepository.class);
		serviceReportRepository = mock(ServiceReportRepository.class);
		calculateWorkingHourRepository = new CalculateWorkingHourRepositoryImp(serviceReportRepository);
		calculateWorkingHourService = new CalculateWorkingHourServiceImp(calculateWorkingHourRepository);
	}

	@Test
	void testIdentificationTechnicianNotValid() {
		CalculateWorkingHourResponseDTO calculateWorkingHourResponseDTO = this.calculateWorkingHourService
				.calculateWorkingHour("", 60);
		assertTrue(calculateWorkingHourResponseDTO.getError().equals("Debe ingresar la identificación del técnico"));

		calculateWorkingHourResponseDTO = this.calculateWorkingHourService.calculateWorkingHour(null, 60);
		assertTrue(calculateWorkingHourResponseDTO.getError().equals("Debe ingresar la identificación del técnico"));
	}

	@Test
	void testNumberWeekNotValid() {
		CalculateWorkingHourResponseDTO calculateWorkingHourResponseDTO = this.calculateWorkingHourService
				.calculateWorkingHour("1012458615", 60);
		assertTrue(calculateWorkingHourResponseDTO.getError().equals(
				"Debe ingresar un número de semana válido,tiene que ser un igual o mayor a 0 (cero) y no puede ser mayor a 52"));

		calculateWorkingHourResponseDTO = this.calculateWorkingHourService.calculateWorkingHour("1012458615", -20);
		assertTrue(calculateWorkingHourResponseDTO.getError().equals(
				"Debe ingresar un número de semana válido,tiene que ser un igual o mayor a 0 (cero) y no puede ser mayor a 52"));
	}

	@Test
	void testResponseCalculateHours() {
		CalculateWorkingHourResponseDTO calculateWorkingHourResponseDTO = this.calculateWorkingHourService
				.calculateWorkingHour("1012458615", 38);

		assertAll(() -> {
			assertNotNull(calculateWorkingHourResponseDTO.getExtraNightHours());
		}, () -> {
			assertTrue(!calculateWorkingHourResponseDTO.getExtraNightHours().isEmpty());
		}, () -> {
			assertNotNull(calculateWorkingHourResponseDTO.getExtraNormalHours());
		}, () -> {
			assertTrue(!calculateWorkingHourResponseDTO.getExtraNormalHours().isEmpty());
		}, () -> {
			assertNotNull(calculateWorkingHourResponseDTO.getExtraSundayHours());
		}, () -> {
			assertTrue(!calculateWorkingHourResponseDTO.getExtraSundayHours().isEmpty());
		}, () -> {
			assertNotNull(calculateWorkingHourResponseDTO.getNightHours());
		}, () -> {
			assertTrue(!calculateWorkingHourResponseDTO.getNightHours().isEmpty());
		}, () -> {
			assertNotNull(calculateWorkingHourResponseDTO.getNormalHours());
		}, () -> {
			assertTrue(!calculateWorkingHourResponseDTO.getNormalHours().isEmpty());
		}, () -> {
			assertNotNull(calculateWorkingHourResponseDTO.getSundayHours());
		}, () -> {
			assertTrue(!calculateWorkingHourResponseDTO.getSundayHours().isEmpty());
		});
	}
}
