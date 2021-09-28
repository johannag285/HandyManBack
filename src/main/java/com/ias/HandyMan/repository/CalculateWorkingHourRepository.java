package com.ias.HandyMan.repository;

import java.util.List;
import java.util.Optional;

import com.ias.HandyMan.model.CalculateWorkingHourRequest;
import com.ias.HandyMan.model.ServiceReport;

public interface CalculateWorkingHourRepository {
	public Optional<List<ServiceReport>> getQueryNormalHours(CalculateWorkingHourRequest calculateWorkingHourRequest);

	public Optional<List<ServiceReport>> getQueryNightHours(CalculateWorkingHourRequest calculateWorkingHourRequest);

	public Optional<List<ServiceReport>> getQuerySundayHours(CalculateWorkingHourRequest calculateWorkingHourRequest);

	public Optional<List<ServiceReport>> getQueryFilter(CalculateWorkingHourRequest calculateWorkingHourRequest);

	public Optional<List<ServiceReport>> getWeekAndTimeStart(CalculateWorkingHourRequest calculateWorkingHourRequest);

	public Optional<List<ServiceReport>> getQueryWeek(CalculateWorkingHourRequest calculateWorkingHourRequest);

}
