package com.ias.HandyMan.services;

import com.ias.HandyMan.dto.CalculateWorkingHourResponseDTO;

public interface CalculateWorkingHourService {
	public CalculateWorkingHourResponseDTO calculateWorkingHour(String identificationTechnician, int numberWeek);
}
