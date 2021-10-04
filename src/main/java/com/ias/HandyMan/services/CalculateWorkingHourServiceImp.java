package com.ias.HandyMan.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ias.HandyMan.config.Constants;
import com.ias.HandyMan.dto.CalculateWorkingHourResponseDTO;
import com.ias.HandyMan.model.CalculateWorkingHourRequest;
import com.ias.HandyMan.model.ServiceReport;
import com.ias.HandyMan.repository.CalculateWorkingHourRepository;

@Service
public class CalculateWorkingHourServiceImp implements CalculateWorkingHourService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CalculateWorkingHourService.class);
	private CalculateWorkingHourRepository calculateWorkingHourRepository;
	private long testUnit;

	public CalculateWorkingHourServiceImp(CalculateWorkingHourRepository calculateWorkingHourRepository) {
		this.testUnit = 0;
		this.calculateWorkingHourRepository = calculateWorkingHourRepository;
	}

	@Override
	public CalculateWorkingHourResponseDTO calculateWorkingHour(String identificationTechnician, int numberWeek) {
		CalculateWorkingHourResponseDTO calculateWorkingHourResponseDTO = new CalculateWorkingHourResponseDTO();

		if (identificationTechnician == null || identificationTechnician.isEmpty()) {
			calculateWorkingHourResponseDTO.setError("Debe ingresar la identificación del técnico");
			return calculateWorkingHourResponseDTO;
		}

		if (numberWeek > 52 || numberWeek < 0) {
			calculateWorkingHourResponseDTO.setError(
					"Debe ingresar un número de semana válido,tiene que ser un igual o mayor a 0 (cero) y no puede ser mayor a 52");
			return calculateWorkingHourResponseDTO;
		}

		try {
			List<ServiceReport> listServiceReport = null;
			listServiceReport = consultServiceReport(Constants.timeSatartNormalHours, Constants.timeEndNormalHours,
					numberWeek, identificationTechnician, Constants.typeNormalHours);
			String normalHours = calculateHours(listServiceReport, false, Constants.typeNormalHours);

			listServiceReport = consultServiceReport(Constants.timeSatartNightHours, Constants.timeEndNightHours,
					numberWeek, identificationTechnician, Constants.typeNightHours);

			String nightHours = calculateHours(listServiceReport, false, Constants.typeNightHours);

			listServiceReport = consultServiceReport(Constants.timeSatartSundayHours, Constants.timeEndSundayHours,
					numberWeek, identificationTechnician, Constants.typeSundayHours);
			String sundayHours = calculateHours(listServiceReport, false, Constants.typeSundayHours);

			listServiceReport = consultServiceReport(Constants.timeSatartNormalHours, Constants.timeEndNormalHours,
					numberWeek, identificationTechnician, Constants.typeExtraNormalHours);
			String extraNormalHours = calculateHours(listServiceReport, true, Constants.typeExtraNormalHours);

			listServiceReport = consultServiceReport(Constants.timeSatartNightHours, Constants.timeEndNightHours,
					numberWeek, identificationTechnician, Constants.typeExtraNightHours);
			String extraNightHours = calculateHours(listServiceReport, true, Constants.typeExtraNightHours);

			listServiceReport = consultServiceReport(Constants.timeSatartSundayHours, Constants.timeEndSundayHours,
					numberWeek, identificationTechnician, Constants.typeExtraSundayHours);
			String extraSundayHours = calculateHours(listServiceReport, true, Constants.typeExtraSundayHours);

			calculateWorkingHourResponseDTO = new CalculateWorkingHourResponseDTO();
			calculateWorkingHourResponseDTO.setNormalHours(normalHours);
			calculateWorkingHourResponseDTO.setNightHours(nightHours);
			calculateWorkingHourResponseDTO.setSundayHours(sundayHours);
			calculateWorkingHourResponseDTO.setExtraNormalHours(extraNormalHours);
			calculateWorkingHourResponseDTO.setExtraNightHours(extraNightHours);
			calculateWorkingHourResponseDTO.setExtraSundayHours(extraSundayHours);
			calculateWorkingHourResponseDTO.setError("");
		} catch (Exception e) {
			LOGGER.error("error", e.getMessage());
			calculateWorkingHourResponseDTO = new CalculateWorkingHourResponseDTO();
			calculateWorkingHourResponseDTO.setError(e.getMessage());
			calculateWorkingHourResponseDTO.setNormalHours("00:00:00");
			calculateWorkingHourResponseDTO.setNightHours("00:00:00");
			calculateWorkingHourResponseDTO.setSundayHours("00:00:00");
			calculateWorkingHourResponseDTO.setExtraNormalHours("00:00:00");
			calculateWorkingHourResponseDTO.setExtraNightHours("00:00:00");
			calculateWorkingHourResponseDTO.setExtraSundayHours("00:00:00");
		}

		return calculateWorkingHourResponseDTO;
	}

	private List<ServiceReport> consultServiceReport(String timeSatart, String timeEnd, int numberWeek,
			String identificationTechnician, String typetypeWorkingHours) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatterHour = new SimpleDateFormat("HH:mm:ss");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.WEEK_OF_YEAR, numberWeek);
		cal.setTime(cal.getTime());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		String startDateTime = sdf.format(cal.getTime());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

		String endDateTime = sdf.format(cal.getTime());
		List<ServiceReport> listServiceReport = new ArrayList<ServiceReport>();

		Date dateStart = formatterDate.parse(startDateTime);
		Date dateEnd = formatterDate.parse(endDateTime);

		Date timeStartParse = formatterHour.parse(timeSatart);
		Date endStartParse = formatterHour.parse(timeEnd);

		CalculateWorkingHourRequest calculateWorkingHourRequest = new CalculateWorkingHourRequest();
		calculateWorkingHourRequest.setEndDateTime(dateEnd);
		calculateWorkingHourRequest.setStartDateTime(dateStart);
		calculateWorkingHourRequest.setIdentificationTechnician(identificationTechnician);
		calculateWorkingHourRequest.setEndTime(endStartParse);
		calculateWorkingHourRequest.setStartTime(timeStartParse);

		if (typetypeWorkingHours.equals(Constants.typeNormalHours)) {
			listServiceReport = calculateWorkingHourRepository.getQueryWeek(calculateWorkingHourRequest).get();
		} else if (typetypeWorkingHours.equals(Constants.typeNightHours)) {
			listServiceReport = calculateWorkingHourRepository.getQueryWeek(calculateWorkingHourRequest).get();
		} else if (typetypeWorkingHours.equals(Constants.typeSundayHours)) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.WEEK_OF_YEAR, numberWeek);
			calendar.setTime(calendar.getTime());
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

			startDateTime = sdf.format(calendar.getTime());
			endDateTime = sdf.format(calendar.getTime());

			dateStart = formatterDate.parse(startDateTime);
			dateEnd = formatterDate.parse(endDateTime);

			calculateWorkingHourRequest.setEndDateTime(dateEnd);
			calculateWorkingHourRequest.setStartDateTime(dateStart);
			listServiceReport = calculateWorkingHourRepository.getQueryWeek(calculateWorkingHourRequest).get();
		} else if (typetypeWorkingHours.equals(Constants.typeExtraNormalHours)) {
			listServiceReport = calculateWorkingHourRepository.getQueryWeekExtraTime(calculateWorkingHourRequest).get();
		} else if (typetypeWorkingHours.equals(Constants.typeExtraNightHours)) {
			listServiceReport = calculateWorkingHourRepository.getQueryWeekExtraTime(calculateWorkingHourRequest).get();
		} else if (typetypeWorkingHours.equals(Constants.typeExtraSundayHours)) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.WEEK_OF_YEAR, numberWeek);
				calendar.setTime(calendar.getTime());
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

				startDateTime = sdf.format(calendar.getTime());
				endDateTime = sdf.format(calendar.getTime());

				dateStart = formatterDate.parse(startDateTime);
				dateEnd = formatterDate.parse(endDateTime);

				calculateWorkingHourRequest.setEndDateTime(dateEnd);
				calculateWorkingHourRequest.setStartDateTime(dateStart);
				listServiceReport = calculateWorkingHourRepository
						.getQuerySundayHoursExtraTime(calculateWorkingHourRequest).get();
		}

		return listServiceReport;
	}

	private String calculateHours(List<ServiceReport> listServiceReport, boolean calculateExtrasHours,
			String typeWorkingHours) {
		String totalnormalHours = "";
		testUnit = 0;

		listServiceReport.forEach(reportService -> {
			Calendar calendarStartTime = Calendar.getInstance();
			calendarStartTime.setTime(reportService.getStartDateTime());

			Calendar calendarEndTime = Calendar.getInstance();
			calendarEndTime.setTime(reportService.getEndDateTime());

			int hourDayStart = 0;
			int minuteDayStart = 0;
			int secondDayStart = 0;

			int hourDayEnd = 0;
			int minuteDayEnd = 0;
			int secondDayEnd = 0;

			LocalDateTime fromDateTime = null;
			LocalDateTime toDateTime = null;
			if ((typeWorkingHours.equals(Constants.typeNormalHours)
					|| typeWorkingHours.equals(Constants.typeExtraNormalHours))) {

				if (calendarEndTime.get(Calendar.DAY_OF_MONTH) > calendarStartTime.get(Calendar.DAY_OF_MONTH)
						&& calendarStartTime.get(Calendar.HOUR_OF_DAY) <= 7
						&& calendarEndTime.get(Calendar.HOUR_OF_DAY) >= 20) {
					/* primera fecha */

					hourDayStart = calendarStartTime.get(Calendar.HOUR_OF_DAY) < 7 ? 7
							: calendarStartTime.get(Calendar.HOUR_OF_DAY);
					minuteDayStart = calendarStartTime.get(Calendar.HOUR_OF_DAY) < 7
							&& calendarEndTime.get(Calendar.MINUTE) > 0 ? 0 : calendarEndTime.get(Calendar.MINUTE);
					secondDayStart = calendarStartTime.get(Calendar.HOUR_OF_DAY) < 7
							&& calendarEndTime.get(Calendar.SECOND) > 0 ? 0 : calendarEndTime.get(Calendar.SECOND);

					calendarStartTime.set(Calendar.HOUR_OF_DAY, hourDayStart);
					calendarStartTime.set(Calendar.MINUTE, minuteDayStart);
					calendarStartTime.set(Calendar.SECOND, secondDayStart);
					fromDateTime = calculateLocalDateTime(calendarStartTime);

					calendarEndTime.setTime(reportService.getStartDateTime());
					calendarEndTime.set(Calendar.HOUR_OF_DAY, 20);
					calendarEndTime.set(Calendar.MINUTE, 0);
					calendarEndTime.set(Calendar.SECOND, 0);
					toDateTime = calculateLocalDateTime(calendarEndTime);

					Duration duration = Duration.between(fromDateTime, toDateTime);
					testUnit = testUnit + duration.toMillis();

					/* Segunda Fecha */
					calendarStartTime.setTime(reportService.getEndDateTime());
					calendarEndTime.setTime(reportService.getEndDateTime());

					hourDayStart = calendarStartTime.get(Calendar.HOUR_OF_DAY) > 7 ? 7
							: calendarStartTime.get(Calendar.HOUR_OF_DAY);
					minuteDayStart = calendarStartTime.get(Calendar.HOUR_OF_DAY) > 7
							&& calendarEndTime.get(Calendar.MINUTE) > 0 ? 0 : calendarEndTime.get(Calendar.MINUTE);
					secondDayStart = calendarStartTime.get(Calendar.HOUR_OF_DAY) > 7
							&& calendarEndTime.get(Calendar.SECOND) > 0 ? 0 : calendarEndTime.get(Calendar.SECOND);

					calendarStartTime.set(Calendar.HOUR_OF_DAY, hourDayStart);
					calendarStartTime.set(Calendar.MINUTE, minuteDayStart);
					calendarStartTime.set(Calendar.SECOND, secondDayStart);
					fromDateTime = calculateLocalDateTime(calendarStartTime);

					calendarEndTime.set(Calendar.HOUR_OF_DAY, 20);
					calendarEndTime.set(Calendar.MINUTE, 0);
					calendarEndTime.set(Calendar.SECOND, 0);
					toDateTime = calculateLocalDateTime(calendarEndTime);

					duration = Duration.between(fromDateTime, toDateTime);
					testUnit = testUnit + duration.toMillis();

				} else if (calendarEndTime.get(Calendar.DAY_OF_MONTH) > calendarStartTime.get(Calendar.DAY_OF_MONTH)) {
					if (calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 7) {
						fromDateTime = calculateLocalDateTime(calendarStartTime);
						calendarEndTime.setTime(reportService.getStartDateTime());

						calendarEndTime.set(Calendar.HOUR_OF_DAY, 20);
						calendarEndTime.set(Calendar.MINUTE, 0);
						calendarEndTime.set(Calendar.SECOND, 0);

						toDateTime = calculateLocalDateTime(calendarEndTime);

						Duration duration = Duration.between(fromDateTime, toDateTime);
						testUnit = testUnit + duration.toMillis();
					}

					calendarStartTime.setTime(reportService.getEndDateTime());
					calendarEndTime.setTime(reportService.getEndDateTime());

					if (calendarEndTime.get(Calendar.HOUR_OF_DAY) >= 7) {
						calendarStartTime.set(Calendar.HOUR_OF_DAY, 7);
						calendarStartTime.set(Calendar.MINUTE, 0);
						calendarStartTime.set(Calendar.SECOND, 0);
						fromDateTime = calculateLocalDateTime(calendarStartTime);

						toDateTime = calculateLocalDateTime(calendarEndTime);
						Duration duration = Duration.between(fromDateTime, toDateTime);
						testUnit = testUnit + duration.toMillis();
					}

				} else if (calendarEndTime.get(Calendar.HOUR_OF_DAY) > 20
						&& calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 7
						&& calendarEndTime.get(Calendar.DAY_OF_MONTH) == calendarStartTime.get(Calendar.DAY_OF_MONTH)) {

					minuteDayEnd = calendarEndTime.get(Calendar.MINUTE) > 0 ? 0 : calendarEndTime.get(Calendar.MINUTE);
					secondDayEnd = calendarEndTime.get(Calendar.SECOND) > 0 ? 0 : calendarEndTime.get(Calendar.SECOND);

					fromDateTime = calculateLocalDateTime(calendarStartTime);

					calendarEndTime.set(Calendar.HOUR_OF_DAY, 20);
					calendarEndTime.set(Calendar.MINUTE, minuteDayEnd);
					calendarEndTime.set(Calendar.SECOND, secondDayEnd);
					toDateTime = calculateLocalDateTime(calendarEndTime);

					Duration duration = Duration.between(fromDateTime, toDateTime);
					testUnit = testUnit + duration.toMillis();
				} else if (calendarEndTime.get(Calendar.HOUR_OF_DAY) <= 20
						&& calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 7
						&& calendarEndTime.get(Calendar.DAY_OF_MONTH) == calendarStartTime.get(Calendar.DAY_OF_MONTH)) {

					hourDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY);
					minuteDayEnd = calendarEndTime.get(Calendar.MINUTE);
					secondDayEnd = calendarEndTime.get(Calendar.SECOND);

					minuteDayEnd = hourDayEnd == 20 ? 0 : minuteDayEnd;
					secondDayEnd = hourDayEnd == 20 ? 0 : secondDayEnd;

					calendarStartTime.set(Calendar.HOUR_OF_DAY, 7);
					calendarStartTime.set(Calendar.MINUTE, 0);
					calendarStartTime.set(Calendar.SECOND, 0);
					fromDateTime = calculateLocalDateTime(calendarStartTime);

					calendarEndTime.set(Calendar.HOUR_OF_DAY, hourDayEnd);
					calendarEndTime.set(Calendar.MINUTE, minuteDayEnd);
					calendarEndTime.set(Calendar.SECOND, secondDayEnd);
					toDateTime = calculateLocalDateTime(calendarEndTime);
					Duration duration = Duration.between(fromDateTime, toDateTime);
					testUnit = testUnit + duration.toMillis();

				} else if (calendarStartTime.get(Calendar.HOUR_OF_DAY) < 7
						&& calendarEndTime.get(Calendar.HOUR_OF_DAY) > 20
						&& calendarEndTime.get(Calendar.DAY_OF_MONTH) == calendarStartTime.get(Calendar.DAY_OF_MONTH)) {

					calendarStartTime.set(Calendar.HOUR_OF_DAY, 7);
					calendarStartTime.set(Calendar.MINUTE, 0);
					calendarStartTime.set(Calendar.SECOND, 0);
					fromDateTime = calculateLocalDateTime(calendarStartTime);

					calendarEndTime.set(Calendar.HOUR_OF_DAY, 20);
					calendarEndTime.set(Calendar.MINUTE, 0);
					calendarEndTime.set(Calendar.SECOND, 0);
					toDateTime = calculateLocalDateTime(calendarEndTime);

					Duration duration = Duration.between(fromDateTime, toDateTime);
					testUnit = testUnit + duration.toMillis();
				} else if (calendarStartTime.get(Calendar.HOUR_OF_DAY) < 7
						&& calendarEndTime.get(Calendar.HOUR_OF_DAY) <= 20
						&& calendarEndTime.get(Calendar.DAY_OF_MONTH) == calendarStartTime.get(Calendar.DAY_OF_MONTH)) {

					hourDayStart = calendarStartTime.get(Calendar.HOUR_OF_DAY) < 7 ? 7
							: calendarStartTime.get(Calendar.HOUR_OF_DAY);

					calendarStartTime.set(Calendar.HOUR_OF_DAY, hourDayStart);
					calendarStartTime.set(Calendar.MINUTE, 0);
					calendarStartTime.set(Calendar.SECOND, 0);
					fromDateTime = calculateLocalDateTime(calendarStartTime);

					minuteDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY) == 20
							&& calendarEndTime.get(Calendar.MINUTE) > 0 ? 0 : calendarEndTime.get(Calendar.MINUTE);
					secondDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY) == 20
							&& calendarEndTime.get(Calendar.SECOND) > 0 ? 0 : calendarEndTime.get(Calendar.SECOND);

					calendarEndTime.set(Calendar.MINUTE, minuteDayEnd);
					calendarEndTime.set(Calendar.SECOND, secondDayEnd);
					toDateTime = calculateLocalDateTime(calendarEndTime);
					Duration duration = Duration.between(fromDateTime, toDateTime);
					testUnit = testUnit + duration.toMillis();
				}

			} else if (typeWorkingHours.equals(Constants.typeNightHours)
					|| typeWorkingHours.equals(Constants.typeExtraNightHours)) {

				if (calendarEndTime.get(Calendar.DAY_OF_MONTH) > calendarStartTime.get(Calendar.DAY_OF_MONTH)) {
					/* Primera Fecha */
					if (calendarStartTime.get(Calendar.HOUR_OF_DAY) <= 7) {
						fromDateTime = calculateLocalDateTime(calendarStartTime);
						calendarEndTime.setTime(reportService.getStartDateTime());
						hourDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY) <= 7 ? 7
								: calendarStartTime.get(Calendar.HOUR_OF_DAY);

						calendarEndTime.set(Calendar.HOUR_OF_DAY, hourDayEnd);
						toDateTime = calculateLocalDateTime(calendarEndTime);

						Duration duration = Duration.between(fromDateTime, toDateTime);
						testUnit = testUnit + duration.toMillis();
					}

					calendarStartTime.setTime(reportService.getStartDateTime());
					calendarEndTime.setTime(reportService.getStartDateTime());

					if (calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 7) {
						hourDayStart = calendarStartTime.get(Calendar.HOUR_OF_DAY);
						minuteDayStart = calendarStartTime.get(Calendar.MINUTE);
						secondDayStart = calendarStartTime.get(Calendar.SECOND);

						calendarStartTime.set(Calendar.HOUR_OF_DAY, 20);
						calendarStartTime.set(Calendar.MINUTE, 0);
						calendarStartTime.set(Calendar.SECOND, 0);
						fromDateTime = calculateLocalDateTime(calendarStartTime);

						calendarEndTime.set(Calendar.HOUR_OF_DAY, 23);
						calendarEndTime.set(Calendar.MINUTE, 59);
						calendarEndTime.set(Calendar.SECOND, 59);
						toDateTime = calculateLocalDateTime(calendarEndTime);
						Duration duration = Duration.between(fromDateTime, toDateTime);
						testUnit = testUnit + duration.toMillis();
					}

					calendarStartTime.setTime(reportService.getStartDateTime());
					calendarEndTime.setTime(reportService.getStartDateTime());

					if (calendarStartTime.get(Calendar.HOUR_OF_DAY) < 7) {
						hourDayStart = calendarStartTime.get(Calendar.HOUR_OF_DAY);
						minuteDayStart = calendarStartTime.get(Calendar.MINUTE);
						secondDayStart = calendarStartTime.get(Calendar.SECOND);

						calendarStartTime.set(Calendar.HOUR_OF_DAY, 20);
						calendarStartTime.set(Calendar.MINUTE, 0);
						calendarStartTime.set(Calendar.SECOND, 0);
						fromDateTime = calculateLocalDateTime(calendarStartTime);

						calendarEndTime.set(Calendar.HOUR_OF_DAY, 23);
						calendarEndTime.set(Calendar.MINUTE, 59);
						calendarEndTime.set(Calendar.SECOND, 59);
						toDateTime = calculateLocalDateTime(calendarEndTime);
						Duration duration = Duration.between(fromDateTime, toDateTime);
						testUnit = testUnit + duration.toMillis();
					}

					calendarStartTime.setTime(reportService.getStartDateTime());
					calendarEndTime.setTime(reportService.getStartDateTime());
					if (calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 20) {
						fromDateTime = calculateLocalDateTime(calendarStartTime);

						calendarEndTime.setTime(reportService.getStartDateTime());

						calendarEndTime.set(Calendar.HOUR_OF_DAY, 23);
						calendarEndTime.set(Calendar.MINUTE, 59);
						calendarEndTime.set(Calendar.SECOND, 59);
						toDateTime = calculateLocalDateTime(calendarEndTime);
						Duration duration = Duration.between(fromDateTime, toDateTime);
						testUnit = testUnit + duration.toMillis();
					}

					/* Segunda fecha */

					calendarStartTime.setTime(reportService.getEndDateTime());
					calendarEndTime.setTime(reportService.getEndDateTime());

					if (calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 0) {
						if (calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 7) {
							calendarStartTime.set(Calendar.HOUR_OF_DAY, 0);
							calendarStartTime.set(Calendar.MINUTE, 0);
							calendarStartTime.set(Calendar.SECOND, 0);
							fromDateTime = calculateLocalDateTime(calendarStartTime);

							hourDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY) > 7 ? 7
									: calendarEndTime.get(Calendar.HOUR_OF_DAY);
							minuteDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY) >= 7 ? 0
									: calendarEndTime.get(Calendar.MINUTE);
							secondDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY) >= 7 ? 0
									: calendarEndTime.get(Calendar.SECOND);

							calendarEndTime.set(Calendar.HOUR_OF_DAY, hourDayEnd);
							calendarEndTime.set(Calendar.MINUTE, minuteDayEnd);
							calendarEndTime.set(Calendar.SECOND, secondDayEnd);

							toDateTime = calculateLocalDateTime(calendarEndTime);

							Duration duration = Duration.between(fromDateTime, toDateTime);
							testUnit = testUnit + duration.toMillis();
						} else if (calendarStartTime.get(Calendar.HOUR_OF_DAY) <= 7) {
							minuteDayStart = calendarStartTime.get(Calendar.MINUTE) > 0
									&& calendarStartTime.get(Calendar.HOUR_OF_DAY) == 7 ? 0
											: calendarStartTime.get(Calendar.MINUTE);
							secondDayEnd = calendarStartTime.get(Calendar.SECOND) > 0
									&& calendarStartTime.get(Calendar.HOUR_OF_DAY) == 7 ? 0
											: calendarStartTime.get(Calendar.SECOND);

							calendarStartTime.set(Calendar.HOUR_OF_DAY, 0);
							calendarStartTime.set(Calendar.MINUTE, minuteDayStart);
							calendarStartTime.set(Calendar.SECOND, secondDayEnd);
							fromDateTime = calculateLocalDateTime(calendarStartTime);

							toDateTime = calculateLocalDateTime(calendarEndTime);
							Duration duration = Duration.between(fromDateTime, toDateTime);
							testUnit = testUnit + duration.toMillis();
						}
					}

					calendarStartTime.setTime(reportService.getEndDateTime());
					calendarEndTime.setTime(reportService.getEndDateTime());

					if (calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 20) {
						minuteDayEnd = calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 20
								&& calendarStartTime.get(Calendar.MINUTE) > 0 ? 0
										: calendarEndTime.get(Calendar.MINUTE);
						secondDayEnd = calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 20
								&& calendarStartTime.get(Calendar.SECOND) > 0 ? 0
										: calendarEndTime.get(Calendar.SECOND);

						calendarStartTime.set(Calendar.HOUR_OF_DAY, 20);
						calendarStartTime.set(Calendar.MINUTE, minuteDayEnd);
						calendarStartTime.set(Calendar.SECOND, secondDayEnd);
						fromDateTime = calculateLocalDateTime(calendarStartTime);

						calendarEndTime.set(Calendar.HOUR_OF_DAY, calendarEndTime.get(Calendar.HOUR_OF_DAY));
						calendarEndTime.set(Calendar.MINUTE, calendarEndTime.get(Calendar.MINUTE));
						calendarEndTime.set(Calendar.SECOND, calendarEndTime.get(Calendar.SECOND));
						toDateTime = calculateLocalDateTime(calendarEndTime);

						Duration duration = Duration.between(fromDateTime, toDateTime);
						testUnit = testUnit + duration.toMillis();
					}

				} else if (calendarEndTime.get(Calendar.DAY_OF_MONTH) == calendarStartTime.get(Calendar.DAY_OF_MONTH)
						&& (calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 0
								&& calendarStartTime.get(Calendar.HOUR_OF_DAY) <= 7)
						&& calendarEndTime.get(Calendar.HOUR_OF_DAY) >= 20) {

					/* horas en la mañana */

					minuteDayStart = calendarStartTime.get(Calendar.MINUTE) > 0
							&& calendarStartTime.get(Calendar.HOUR_OF_DAY) == 7 ? 0
									: calendarStartTime.get(Calendar.MINUTE);
					secondDayEnd = calendarStartTime.get(Calendar.SECOND) > 0
							&& calendarStartTime.get(Calendar.HOUR_OF_DAY) == 7 ? 0
									: calendarStartTime.get(Calendar.SECOND);

					calendarStartTime.set(Calendar.HOUR_OF_DAY, calendarStartTime.get(Calendar.HOUR_OF_DAY));
					calendarStartTime.set(Calendar.MINUTE, minuteDayStart);
					calendarStartTime.set(Calendar.SECOND, secondDayEnd);
					fromDateTime = calculateLocalDateTime(calendarStartTime);

					hourDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY) > 7 ? 7
							: calendarEndTime.get(Calendar.HOUR_OF_DAY);
					minuteDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY) >= 7 ? 0
							: calendarEndTime.get(Calendar.MINUTE);
					secondDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY) >= 7 ? 0
							: calendarEndTime.get(Calendar.SECOND);

					calendarEndTime.set(Calendar.HOUR_OF_DAY, hourDayEnd);
					calendarEndTime.set(Calendar.MINUTE, minuteDayEnd);
					calendarEndTime.set(Calendar.SECOND, secondDayEnd);

					toDateTime = calculateLocalDateTime(calendarEndTime);
					Duration duration = Duration.between(fromDateTime, toDateTime);
					testUnit = testUnit + duration.toMillis();

					/* Horas en la noche */
					calendarStartTime.setTime(reportService.getStartDateTime());
					calendarEndTime.setTime(reportService.getEndDateTime());

					if (calendarEndTime.get(Calendar.HOUR_OF_DAY) > 20) {
						calendarStartTime.set(Calendar.HOUR_OF_DAY, 20);
						calendarStartTime.set(Calendar.MINUTE, 0);
						calendarStartTime.set(Calendar.SECOND, 0);
						fromDateTime = calculateLocalDateTime(calendarStartTime);
					} else if (calendarEndTime.get(Calendar.HOUR_OF_DAY) == 20) {
						fromDateTime = calculateLocalDateTime(calendarEndTime);
					}

					toDateTime = calculateLocalDateTime(calendarEndTime);
					duration = Duration.between(fromDateTime, toDateTime);
					testUnit = testUnit + duration.toMillis();
				} else if (calendarEndTime.get(Calendar.DAY_OF_MONTH) == calendarStartTime.get(Calendar.DAY_OF_MONTH)
						&& calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 0
						&& calendarEndTime.get(Calendar.HOUR_OF_DAY) <= 7) {

					minuteDayStart = calendarStartTime.get(Calendar.MINUTE) > 0
							&& calendarStartTime.get(Calendar.HOUR_OF_DAY) == 7 ? 0
									: calendarStartTime.get(Calendar.MINUTE);
					secondDayEnd = calendarStartTime.get(Calendar.SECOND) > 0
							&& calendarStartTime.get(Calendar.HOUR_OF_DAY) == 7 ? 0
									: calendarStartTime.get(Calendar.SECOND);

					calendarStartTime.set(Calendar.MINUTE, minuteDayStart);
					calendarStartTime.set(Calendar.SECOND, secondDayEnd);
					fromDateTime = calculateLocalDateTime(calendarStartTime);
					toDateTime = calculateLocalDateTime(calendarEndTime);
					Duration duration = Duration.between(fromDateTime, toDateTime);
					testUnit = testUnit + duration.toMillis();
				} else if (calendarEndTime.get(Calendar.DAY_OF_MONTH) == calendarStartTime.get(Calendar.DAY_OF_MONTH)
						&& calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 20) {
					minuteDayStart = calendarStartTime.get(Calendar.MINUTE) > 0
							&& calendarStartTime.get(Calendar.HOUR_OF_DAY) == 20 ? 0
									: calendarStartTime.get(Calendar.MINUTE);
					secondDayEnd = calendarStartTime.get(Calendar.SECOND) > 0
							&& calendarStartTime.get(Calendar.HOUR_OF_DAY) == 20 ? 0
									: calendarStartTime.get(Calendar.SECOND);

					calendarStartTime.set(Calendar.MINUTE, minuteDayStart);
					calendarStartTime.set(Calendar.SECOND, secondDayEnd);
					fromDateTime = calculateLocalDateTime(calendarStartTime);
					toDateTime = calculateLocalDateTime(calendarEndTime);
					Duration duration = Duration.between(fromDateTime, toDateTime);
					testUnit = testUnit + duration.toMillis();
				} else if (calendarEndTime.get(Calendar.DAY_OF_MONTH) == calendarStartTime.get(Calendar.DAY_OF_MONTH)
						&& calendarStartTime.get(Calendar.HOUR_OF_DAY) >= 0
						&& calendarStartTime.get(Calendar.HOUR_OF_DAY) <= 7) {

					minuteDayStart = calendarStartTime.get(Calendar.MINUTE) > 0
							&& calendarStartTime.get(Calendar.HOUR_OF_DAY) == 7 ? 0
									: calendarStartTime.get(Calendar.MINUTE);
					secondDayEnd = calendarStartTime.get(Calendar.SECOND) > 0
							&& calendarStartTime.get(Calendar.HOUR_OF_DAY) == 7 ? 0
									: calendarStartTime.get(Calendar.SECOND);

					calendarStartTime.set(Calendar.MINUTE, minuteDayStart);
					calendarStartTime.set(Calendar.SECOND, secondDayEnd);
					fromDateTime = calculateLocalDateTime(calendarStartTime);

					if (calendarEndTime.get(Calendar.HOUR_OF_DAY) >= 7) {
						hourDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY) > 7 ? 7
								: calendarEndTime.get(Calendar.HOUR_OF_DAY);
						minuteDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY) >= 7
								&& calendarStartTime.get(Calendar.MINUTE) > 0 ? 0
										: calendarStartTime.get(Calendar.MINUTE);
						secondDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY) >= 7
								&& calendarStartTime.get(Calendar.SECOND) > 0 ? 0
										: calendarStartTime.get(Calendar.SECOND);

						calendarEndTime.set(Calendar.HOUR_OF_DAY, hourDayEnd);
						calendarEndTime.set(Calendar.MINUTE, minuteDayEnd);
						calendarEndTime.set(Calendar.SECOND, secondDayEnd);
					}

					toDateTime = calculateLocalDateTime(calendarEndTime);
					Duration duration = Duration.between(fromDateTime, toDateTime);
					testUnit = testUnit + duration.toMillis();
				}
			} else if (typeWorkingHours.equals(Constants.typeSundayHours)
					|| typeWorkingHours.equals(Constants.typeExtraSundayHours)) {
				fromDateTime = calculateLocalDateTime(calendarStartTime);
				toDateTime = calculateLocalDateTime(calendarEndTime);

				Duration duration = Duration.between(fromDateTime, toDateTime);
				testUnit = testUnit + duration.toMillis();
			} else {
				fromDateTime = calculateLocalDateTime(calendarStartTime);
				toDateTime = calculateLocalDateTime(calendarEndTime);

				Duration duration = Duration.between(fromDateTime, toDateTime);
				testUnit = testUnit + duration.toMillis();
			}

			/*
			 * LocalTime hackUseOfClockAsDuration = LocalTime.MIN.plus(duration); totalHours
			 * = totalHours + hackUseOfClockAsDuration.getHour(); totalMinutes =
			 * totalMinutes + hackUseOfClockAsDuration.getMinute(); totalSeconds =
			 * totalSeconds + hackUseOfClockAsDuration.getSecond();
			 */
		});

		long millis = testUnit;
		String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		totalnormalHours = hms;

		String splitHours[] = totalnormalHours.split(":");
		int hoursCalculate = Integer.parseInt(splitHours[0]);
		int minutes = Integer.parseInt(splitHours[1]);

		if (hoursCalculate < 0 || minutes < 0) {
			totalnormalHours = "00:00:00";
		}

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
