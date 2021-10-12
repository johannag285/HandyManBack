package com.ias.HandyMan.repository.database;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ias.HandyMan.model.ServiceReport;



@Repository
public interface ServiceReportRepository extends JpaRepository<ServiceReport, Integer> {
	List<ServiceReport> findByIdentificationTechnicianAndStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(String identificationTechnician, Date startDateTime, Date endDateTime);
	List<ServiceReport> findByStartDateTimeGreaterThanEqual(Date startDateTime);
	List<ServiceReport> findByStartDateTimeEquals(Date startDateTime);
	List<ServiceReport> findAllByStartDateTimeAndEndDateTime(Date startDateTime, Date endDateTime);
	
	@Query("select a from ServiceReport a where a.identificationTechnician = :identificationTechnician and DATE(a.startDateTime) >= :startDate and  TIME(a.startDateTime) >= :startTime and TIME(a.endDateTime) <= :endTime and DATE(a.endDateTime) <= :endDate")
    Optional<List<ServiceReport>> queryFilter(@Param("identificationTechnician") String identificationTechnician, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
	
	
	@Query("select a from ServiceReport a where a.identificationTechnician = :identificationTechnician and DATE(a.startDateTime) >= :startDate and  TIME(a.startDateTime) >= :startTime and TIME(a.startDateTime) <= :endTime and DATE(a.endDateTime) <= :endDate")
    Optional<List<ServiceReport>> queryNormalHours(@Param("identificationTechnician") String identificationTechnician, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
	
	@Query("select a from ServiceReport a where a.identificationTechnician = :identificationTechnician and DATE(a.startDateTime) >= :startDate and  TIME(a.startDateTime) >= :startTime and DATE(a.endDateTime) <= :endDate")
	 Optional< List<ServiceReport>> queryNigthHours(@Param("identificationTechnician") String identificationTechnician, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("startTime") Date startTime);
      
	
	@Query("select a from ServiceReport a where a.identificationTechnician = :identificationTechnician and DATE(a.startDateTime) >= :startDate and isEXtraTime=0")
	Optional<List<ServiceReport>> querySundayHours(@Param("identificationTechnician") String identificationTechnician, @Param("startDate") Date startDate);
	
	@Query("select a from ServiceReport a where a.identificationTechnician = :identificationTechnician and DATE(a.startDateTime) >= :startDate and isEXtraTime=1")
	Optional<List<ServiceReport>> querySundayHoursExtraTime(@Param("identificationTechnician") String identificationTechnician, @Param("startDate") Date startDate);
	
	
	@Query("select a from ServiceReport a where a.identificationTechnician = :identificationTechnician and DATE(a.startDateTime) >= :startDate and DATE(a.endDateTime) <= :endDate and isEXtraTime=0")
	Optional<List<ServiceReport>> queryWeek(@Param("identificationTechnician") String identificationTechnician, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	@Query("select a from ServiceReport a where a.identificationTechnician = :identificationTechnician and DATE(a.startDateTime) >= :startDate and DATE(a.endDateTime) <= :endDate and isEXtraTime=1")
	Optional<List<ServiceReport>> queryWeekExtraTime(@Param("identificationTechnician") String identificationTechnician, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	
	@Query("select a from ServiceReport a where a.identificationTechnician = :identificationTechnician and DATE(a.startDateTime) >= :startDate and DATE(a.endDateTime) <= :endDate")
	Optional<List<ServiceReport>> queryWeekAndTimeStart(@Param("identificationTechnician") String identificationTechnician, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

	
	@Query("select a from ServiceReport a where a.identificationTechnician = :identificationTechnician and DATE(a.startDateTime) >= :startDate and DATE(a.endDateTime) <= :endDate and isEXtraTime=0")
	Optional<List<ServiceReport>> queryWeekOther(@Param("identificationTechnician") String identificationTechnician, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
