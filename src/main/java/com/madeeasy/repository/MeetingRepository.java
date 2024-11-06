package com.madeeasy.repository;


import com.madeeasy.entity.Employee;
import com.madeeasy.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByParticipantsInAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            List<Employee> participants, LocalDateTime start, LocalDateTime end);

    @Query("SELECT m FROM Meeting m JOIN m.participants p WHERE p.id = :employeeId ORDER BY m.startTime")
    List<Meeting> findByEmployeeOrderByStartTime(@Param("employeeId") Long employeeId);

    @Query("SELECT m FROM Meeting m JOIN m.participants p WHERE p.id IN :employeeIds")
    List<Meeting> findByEmployeeIds(List<Long> employeeIds);

    @Query("SELECT m FROM Meeting m WHERE m.startTime < :requestedEndTime AND m.endTime > :requestedStartTime")
    List<Meeting> findMeetingsInTimeRange(@Param("requestedStartTime") LocalDateTime requestedStartTime,
                                          @Param("requestedEndTime") LocalDateTime requestedEndTime);

    @Query("SELECT m FROM Meeting m JOIN m.participants p WHERE p.id = :employeeId")
    List<Meeting> findByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM Meeting m " +
            "JOIN m.participants p " +
            "WHERE p.id = :employeeId " +
            "AND m.startTime < :requestedEndTime " +
            "AND m.endTime > :requestedStartTime")
    boolean existsByEmployeeIdAndTimeRange(@Param("employeeId") Long employeeId,
                                           @Param("requestedStartTime") LocalDateTime requestedStartTime,
                                           @Param("requestedEndTime") LocalDateTime requestedEndTime);

}
