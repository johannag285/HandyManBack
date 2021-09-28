package com.ias.HandyMan.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
	private int totalHours;
	private int totalMinutes;
	private int totalSeconds;
	private long testUnit;
	private int differenceHoursWeek;

	public CalculateWorkingHourServiceImp(CalculateWorkingHourRepository calculateWorkingHourRepository) {
		this.totalHours = 0;
		this.totalMinutes = 0;
		this.totalSeconds = 0;
		this.testUnit = 0;
		this.differenceHoursWeek = 0;
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
			listServiceReport = calculateWorkingHourRepository.getQueryNormalHours(calculateWorkingHourRequest).get();
		}else if (typetypeWorkingHours.equals(Constants.typeNightHours)) {
			listServiceReport = calculateWorkingHourRepository.getQueryNightHours(calculateWorkingHourRequest).get();
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
			listServiceReport = calculateWorkingHourRepository.getQuerySundayHours(calculateWorkingHourRequest).get();
		} else if (typetypeWorkingHours.equals(Constants.typeExtraNormalHours)) {
			List<ServiceReport> listServiceReportWeek = calculateWorkingHourRepository
					.getQueryWeek(calculateWorkingHourRequest).get();
			String hours = calculateHours(listServiceReportWeek, false, "");
			String splitHour[] = hours.split(":");
			if (Integer.valueOf(splitHour[0]) > 48) {
				differenceHoursWeek = Integer.valueOf(splitHour[0])- 48;
				listServiceReport = calculateWorkingHourRepository.getQueryNormalHours(calculateWorkingHourRequest)
						.get();
			}
		} else if (typetypeWorkingHours.equals(Constants.typeExtraNightHours)) {
			List<ServiceReport> listServiceReportWeek = calculateWorkingHourRepository
					.getQueryWeek(calculateWorkingHourRequest).get();
			String hours = calculateHours(listServiceReportWeek, false, "");
			String splitHour[] = hours.split(":");

			if (Integer.valueOf(splitHour[0]) > 48) {
				differenceHoursWeek = Integer.valueOf(splitHour[0])- 48;
				listServiceReport = calculateWorkingHourRepository.getQueryNightHours(calculateWorkingHourRequest)
						.get();
			}
		} else if (typetypeWorkingHours.equals(Constants.typeExtraSundayHours)) {
			List<ServiceReport> listServiceReportWeek = calculateWorkingHourRepository
					.getQueryWeek(calculateWorkingHourRequest).get();
			String hours = calculateHours(listServiceReportWeek, false, "");
			String splitHour[] = hours.split(":");

			if (Integer.valueOf(splitHour[0]) > 48) {
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
				listServiceReport = calculateWorkingHourRepository.getQuerySundayHours(calculateWorkingHourRequest)
						.get();
			}
		}
		return listServiceReport;
	}

	private String calculateHours(List<ServiceReport> listServiceReport, boolean calculateExtrasHours,
			String typeWorkingHours) {
		String totalnormalHours = "";
		totalHours = 0;
		totalMinutes = 0;
		totalSeconds = 0;
		testUnit = 0;

		listServiceReport.forEach(reportService -> {
			Calendar calendarStartTime = Calendar.getInstance();
			calendarStartTime.setTime(reportService.getStartDateTime());

			Calendar calendarEndTime = Calendar.getInstance();
			calendarEndTime.setTime(reportService.getEndDateTime());

			int hourDayEnd = calendarEndTime.get(Calendar.HOUR_OF_DAY);
			int minuteDayEnd = calendarEndTime.get(Calendar.MINUTE);
			int secondDayEnd = calendarEndTime.get(Calendar.SECOND);

			int hourDayStart = calendarStartTime.get(Calendar.HOUR_OF_DAY);
			int minuteDayStart = calendarStartTime.get(Calendar.MINUTE);
			int secondDayStart = calendarStartTime.get(Calendar.SECOND);

			if ((typeWorkingHours.equals(Constants.typeNormalHours)
					|| typeWorkingHours.equals(Constants.typeExtraNormalHours)) && hourDayEnd > 20
					&& calendarEndTime.get(Calendar.DAY_OF_MONTH) == calendarStartTime.get(Calendar.DAY_OF_MONTH)) {
				hourDayEnd = 20;
				minuteDayEnd = minuteDayEnd > 0 ? 0 : minuteDayEnd;
				secondDayEnd = secondDayEnd > 0 ? 0 : secondDayEnd;
			} else if ((typeWorkingHours.equals(Constants.typeNormalHours)
					|| typeWorkingHours.equals(Constants.typeExtraNormalHours)) && hourDayEnd > 0
					&& calendarEndTime.get(Calendar.DAY_OF_MONTH) > calendarStartTime.get(Calendar.DAY_OF_MONTH)) {
				calendarEndTime.setTime(reportService.getStartDateTime());
				hourDayEnd = 20;
				minuteDayEnd = minuteDayEnd < 59 ? minuteDayEnd : 59;
				secondDayEnd = secondDayEnd < 59 ? secondDayEnd : 59;
			} else if ((typeWorkingHours.equals(Constants.typeNightHours)
					|| typeWorkingHours.equals(Constants.typeExtraNightHours))
					&& calendarEndTime.get(Calendar.DAY_OF_MONTH) > calendarStartTime.get(Calendar.DAY_OF_MONTH)
					&& hourDayEnd > 7) {
				hourDayEnd = 7;
				minuteDayEnd = minuteDayEnd > 0 ? 0 : minuteDayEnd;
				secondDayEnd = secondDayEnd > 0 ? 0 : secondDayEnd;
			}

			LocalDateTime fromDateTime = LocalDateTime.of(calendarStartTime.get(Calendar.YEAR),
					calendarStartTime.get(Calendar.MONTH), calendarStartTime.get(Calendar.DAY_OF_MONTH), hourDayStart,
					minuteDayStart, secondDayStart);

			LocalDateTime toDateTime = LocalDateTime.of(calendarEndTime.get(Calendar.YEAR),
					calendarEndTime.get(Calendar.MONTH), calendarEndTime.get(Calendar.DAY_OF_MONTH), hourDayEnd,
					minuteDayEnd, secondDayEnd);

			Duration duration = Duration.between(fromDateTime, toDateTime);

			testUnit = testUnit + duration.toMillis();

			LocalTime hackUseOfClockAsDuration = LocalTime.MIN.plus(duration);
			totalHours = totalHours + hackUseOfClockAsDuration.getHour();
			totalMinutes = totalMinutes + hackUseOfClockAsDuration.getMinute();
			totalSeconds = totalSeconds + hackUseOfClockAsDuration.getSecond();
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

		if (typeWorkingHours.equals(Constants.typeExtraNormalHours)) {
			if (hoursCalculate > 48) {
				hoursCalculate = hoursCalculate - 48;
				/*if(hoursCalculate > 48) {
					hoursCalculate = hoursCalculate - 48;
				}else {
					int diffrenceHoursCalculate = hoursCalculate - differenceHoursWeek;
					hoursCalculate = Math.round((hoursCalculate - diffrenceHoursCalculate) / 2);
				}*/
				
			
				
			//if (differenceHoursWeek > 0) {
				//hoursCalculate = hoursCalculate - 48;
				//int diffrenceHoursCalculate = hoursCalculate - differenceHoursWeek;
				//hoursCalculate = Math.round((hoursCalculate - diffrenceHoursCalculate) / 2);
				totalnormalHours = hoursCalculate + ":" + splitHours[1] + ":" + splitHours[2];
			} else {
				totalnormalHours = "00:00:00";
			}

		} else if (typeWorkingHours.equals(Constants.typeExtraNightHours)) {
			if (hoursCalculate > 48) {
				hoursCalculate = hoursCalculate - 48;
				/*if(hoursCalculate > 48) {
					hoursCalculate = hoursCalculate - 48;
				}else {
					int diffrenceHoursCalculate = hoursCalculate - differenceHoursWeek;
					hoursCalculate = Math.round((hoursCalculate - diffrenceHoursCalculate)/2);
				}*/
				totalnormalHours = hoursCalculate + ":" + splitHours[1] + ":" + splitHours[2];
			} else {
				totalnormalHours = "00:00:00";
			}
		}

		differenceHoursWeek = 0;
		return totalnormalHours;
	}

}
