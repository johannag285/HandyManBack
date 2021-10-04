package com.ias.HandyMan.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "serviceReport")
public class ServiceReport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String identificationTechnician;
	private String identificationService;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDateTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDateTime;
	private boolean isExtraTime;
}
