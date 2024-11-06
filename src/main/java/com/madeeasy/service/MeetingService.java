package com.madeeasy.service;

import com.madeeasy.dto.request.MeetingRequestDTO;
import com.madeeasy.dto.response.MeetingResponseDTO;
import com.madeeasy.entity.CalendarSlot;
import com.madeeasy.entity.Employee;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingService {
    MeetingResponseDTO bookMeeting(MeetingRequestDTO request);

    List<Employee> findConflictedParticipants(LocalDateTime requestedStartTime,
                                              int durationMinutes);

    List<CalendarSlot> getAvailableSlots(List<Long> employeeIds, LocalDateTime requestedStartTime, int durationMinutes);
}
