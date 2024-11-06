package com.madeeasy.repository;


import com.madeeasy.entity.CalendarSlot;
import com.madeeasy.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarSlotRepository extends JpaRepository<CalendarSlot, Long> {
    List<CalendarSlot> findByEmployeeAndIsAvailableTrue(Employee employee);

    boolean existsByEmployeeAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Employee participant, LocalDateTime endTime, LocalDateTime startTime);

    List<CalendarSlot> findByEmployee(Employee employee);

    // Find all calendar slots for a given employee
    @Query("SELECT cs FROM CalendarSlot cs WHERE cs.employee.id = :employeeId")
    List<CalendarSlot> findByEmployeeId(Long employeeId); // Query using employeeId
}