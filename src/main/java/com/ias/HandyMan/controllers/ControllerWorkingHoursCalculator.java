package com.ias.HandyMan.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ias.HandyMan.dto.CalculateWorkingHourResponseDTO;
import com.ias.HandyMan.dto.ServiceReportRequestDTO;
import com.ias.HandyMan.dto.ServiceReportResponseDTO;
import com.ias.HandyMan.services.CalculateWorkingHourService;
import com.ias.HandyMan.services.ReportService;

@RestController
@RequestMapping("/ws/workingHoursCalculator")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:9876"})
public class ControllerWorkingHoursCalculator {

	ReportService reportService;
	CalculateWorkingHourService calculateWorkingHourService;
	@Autowired
	public ControllerWorkingHoursCalculator(ReportService reportServiceImp, CalculateWorkingHourService calculateWorkingHourService) {
		this.reportService = reportServiceImp;
		this.calculateWorkingHourService = calculateWorkingHourService;
	}
	
	@PostMapping("/reportService")
	public ServiceReportResponseDTO reportService(@RequestBody ServiceReportRequestDTO serviceReportRequestDTO) {
		return reportService.addServiceReport(serviceReportRequestDTO);
	}
	
	
	@GetMapping(path = "/calculateWorkingHour")
	public CalculateWorkingHourResponseDTO calculateWorkingHour(@RequestParam String identificationTechnician, @RequestParam int numberWeek) {
		return calculateWorkingHourService.calculateWorkingHour(identificationTechnician, numberWeek);
	}
}
