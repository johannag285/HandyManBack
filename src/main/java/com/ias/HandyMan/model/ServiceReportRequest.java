package com.ias.HandyMan.model;

import java.io.Serializable;
import java.util.Date;

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
public class ServiceReportRequest implements Serializable{
	private static final long serialVersionUID = 4907229684810992637L;
	private String identificationTechnician;
	private String identificationService;
	private Date startDateTime;
	private Date endDateTime;
}
