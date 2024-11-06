package com.madeeasy.controller;

import com.madeeasy.dto.request.MeetingRequestDTO;
import com.madeeasy.dto.response.ConflictResponseDTO;
import com.madeeasy.dto.response.MeetingResponseDTO;
import com.madeeasy.entity.CalendarSlot;
import com.madeeasy.entity.Employee;
import com.madeeasy.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping(path = "/book")
    public ResponseEntity<?> bookMeeting(@RequestBody MeetingRequestDTO request) {
        MeetingResponseDTO meeting = meetingService.bookMeeting(request);
        return ResponseEntity.ok(meeting);
    }

    @GetMapping(path = "/free-slots")
    public ResponseEntity<?> getFreeSlots(
            @RequestParam List<Long> employeeIds,
            @RequestParam LocalDateTime requestedStartTime,
            @RequestParam int durationMinutes) {
        List<CalendarSlot> slots = meetingService.getAvailableSlots(employeeIds, requestedStartTime, durationMinutes);
        return ResponseEntity.ok(slots);
    }


    @PostMapping("/conflicts")
    public ResponseEntity<ConflictResponseDTO> getConflictedParticipants(@RequestParam LocalDateTime requestedStartTime,
                                                                         @RequestParam int durationMinutes) {
        List<Employee> conflicts = meetingService.findConflictedParticipants(requestedStartTime, durationMinutes);
        return ResponseEntity.ok(ConflictResponseDTO.builder().conflictedEmployees(conflicts).build());
    }
}

