package com.ias.HandyMan.dto;

import java.io.Serializable;

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
public class ServiceReportResponseDTO implements Serializable {
	private static final long serialVersionUID = 4907229684810992637L;
	private String response;
}
