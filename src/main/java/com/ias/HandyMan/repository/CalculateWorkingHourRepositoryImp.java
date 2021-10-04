package com.ias.HandyMan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ias.HandyMan.model.CalculateWorkingHourRequest;
import com.ias.HandyMan.model.ServiceReport;
import com.ias.HandyMan.repository.database.ServiceReportRepository;

@Repository
public class CalculateWorkingHourRepositoryImp implements CalculateWorkingHourRepository {
	ServiceReportRepository serviceReportRepository;
	
	public CalculateWorkingHourRepositoryImp(ServiceReportRepository serviceReportRepository) {
		this.serviceReportRepository = serviceReportRepository;
	}
	
	@Override
	public Optional<List<ServiceReport>> getQueryNormalHours(CalculateWorkingHourRequest calculateWorkingHourRequest) {
		return serviceReportRepository.queryNormalHours(calculateWorkingHourRequest.getIdentificationTechnician(),
				calculateWorkingHourRequest.getStartDateTime(), calculateWorkingHourRequest.getEndDateTime(),
				calculateWorkingHourRequest.getStartTime(), calculateWorkingHourRequest.getEndTime());
	}

	@Override
	public Optional<List<ServiceReport>> getQueryNightHours(CalculateWorkingHourRequest calculateWorkingHourRequest) {
		return serviceReportRepository.queryNigthHours(calculateWorkingHourRequest.getIdentificationTechnician(),
				calculateWorkingHourRequest.getStartDateTime(), calculateWorkingHourRequest.getEndDateTime(),
				calculateWorkingHourRequest.getStartTime());
	}

	@Override
	public Optional<List<ServiceReport>> getQuerySundayHours(CalculateWorkingHourRequest calculateWorkingHourRequest) {
		return serviceReportRepository.querySundayHours(calculateWorkingHourRequest.getIdentificationTechnician(),
				calculateWorkingHourRequest.getStartDateTime(), calculateWorkingHourRequest.getEndDateTime());
	}

	@Override
	public Optional<List<ServiceReport>> getQueryFilter(CalculateWorkingHourRequest calculateWorkingHourRequest) {
		return serviceReportRepository.queryFilter(calculateWorkingHourRequest.getIdentificationTechnician(),
				calculateWorkingHourRequest.getStartDateTime(), calculateWorkingHourRequest.getEndDateTime(),
				calculateWorkingHourRequest.getStartTime(), calculateWorkingHourRequest.getEndTime());
	}
	
	
	@Override
	public Optional<List<ServiceReport>> getWeekAndTimeStart(CalculateWorkingHourRequest calculateWorkingHourRequest) {
		return serviceReportRepository.queryWeekAndTimeStart(calculateWorkingHourRequest.getIdentificationTechnician(),
				calculateWorkingHourRequest.getStartDateTime(), calculateWorkingHourRequest.getEndDateTime(),
				calculateWorkingHourRequest.getStartTime(), calculateWorkingHourRequest.getEndTime());
	}

	@Override
	public Optional<List<ServiceReport>> getQueryWeek(CalculateWorkingHourRequest calculateWorkingHourRequest) {
		return serviceReportRepository.queryWeek(calculateWorkingHourRequest.getIdentificationTechnician(),
				calculateWorkingHourRequest.getStartDateTime(), calculateWorkingHourRequest.getEndDateTime());
	}

	@Override
	public Optional<List<ServiceReport>> getQueryWeekExtraTime(
			CalculateWorkingHourRequest calculateWorkingHourRequest) {
		return serviceReportRepository.queryWeekExtraTime(calculateWorkingHourRequest.getIdentificationTechnician(),
				calculateWorkingHourRequest.getStartDateTime(), calculateWorkingHourRequest.getEndDateTime());
	}

	@Override
	public Optional<List<ServiceReport>> getQuerySundayHoursExtraTime(
			CalculateWorkingHourRequest calculateWorkingHourRequest) {
		return serviceReportRepository.querySundayHoursExtraTime(calculateWorkingHourRequest.getIdentificationTechnician(),
				calculateWorkingHourRequest.getStartDateTime(), calculateWorkingHourRequest.getEndDateTime());
	}

}
