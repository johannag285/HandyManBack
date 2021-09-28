package com.ias.HandyMan.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalculateWorkingHourResponse {
	private String normalHours;
	private String nightHours;
	private String sundayHours;
	private String extraNormalHours;
	private String extraNightHours;
	private String extraSundayHours;
}
