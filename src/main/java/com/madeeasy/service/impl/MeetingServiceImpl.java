package com.madeeasy.service.impl;

import com.madeeasy.dto.request.MeetingRequestDTO;
import com.madeeasy.dto.response.CalendarSlotResponseDTO;
import com.madeeasy.dto.response.EmployeeResponseDTO;
import com.madeeasy.dto.response.MeetingResponseDTO;
import com.madeeasy.entity.CalendarSlot;
import com.madeeasy.entity.Employee;
import com.madeeasy.entity.Meeting;
import com.madeeasy.exception.ConflictException;
import com.madeeasy.exception.ResourceNotFoundException;
import com.madeeasy.repository.CalendarSlotRepository;
import com.madeeasy.repository.EmployeeRepository;
import com.madeeasy.repository.MeetingRepository;
import com.madeeasy.service.EmployeeService;
import com.madeeasy.service.MeetingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final EmployeeRepository employeeRepository;
    private final CalendarSlotRepository calendarSlotRepository;
    private final EmployeeService employeeService;

    @Override
    public MeetingResponseDTO bookMeeting(MeetingRequestDTO request) {

        // Validate admin ID
        if (!employeeService.employeeExists(request.getAdminId())) {
            throw new EntityNotFoundException("Admin not found.");
        }

        // Validate participant IDs
        List<Long> invalidIds = request.getParticipantIds().stream()
                .filter(id -> !employeeService.employeeExists(id))
                .toList();

        if (!invalidIds.isEmpty()) {
            throw new EntityNotFoundException("Employees not found with IDs: " + invalidIds);
        }

        // Validate request data
        if (request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
            throw new IllegalArgumentException("Meeting must have at least one participant.");
        }
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }

        // Retrieve participants from database
        List<Employee> participants = new ArrayList<>(employeeRepository.findAllById(request.getParticipantIds()));

        if (participants.size() != request.getParticipantIds().size()) {
            throw new ResourceNotFoundException("One or more participants not found.");
        }

        Employee adminParticipant = this.employeeRepository.findById(request.getAdminId()).get();

        // add admin to participants
        participants.add(adminParticipant);

        // Check for scheduling conflicts in CalendarSlots
        for (Employee participant : participants) {
            boolean hasConflict = calendarSlotRepository.existsByEmployeeAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                    participant, request.getEndTime(), request.getStartTime());
            if (hasConflict) {
                throw new ConflictException("Participant " + participant.getName() + " has a scheduling conflict.");
            }
        }

        // Create and save new meeting if no conflicts
        Meeting meeting = new Meeting();
        meeting.setTopic(request.getTopic());
        meeting.setParticipants(participants);
        meeting.setStartTime(request.getStartTime());
        meeting.setEndTime(request.getEndTime());

        Meeting savedMeeting = meetingRepository.save(meeting);

        // Create calendar slots for each participant
        for (Employee participant : participants) {
            CalendarSlot slot = new CalendarSlot();
            slot.setEmployee(participant);
            slot.setStartTime(request.getStartTime());
            slot.setEndTime(request.getEndTime());
            slot.setAvailable(false);  // Mark the slot as booked
            calendarSlotRepository.save(slot);  // Save the calendar slot
        }

        // Build and return response DTO
        return MeetingResponseDTO.builder()
                .id(savedMeeting.getId())
                .topic(savedMeeting.getTopic())
                .startTime(savedMeeting.getStartTime())
                .endTime(savedMeeting.getEndTime())
                .participants(participants.stream()
                        .map(employee -> EmployeeResponseDTO.builder()
                                .id(employee.getId())
                                .name(employee.getName())
                                .email(employee.getEmail())
                                .calendarSlots(calendarSlotRepository.findByEmployee(employee).stream()
                                        .map(calendarSlot -> CalendarSlotResponseDTO.builder()
                                                .id(calendarSlot.getId())
                                                .startTime(calendarSlot.getStartTime())
                                                .endTime(calendarSlot.getEndTime())
                                                .isAvailable(calendarSlot.isAvailable())
                                                .build())
                                        .toList())
                                .build())
                        .collect(Collectors.toList())
                )
                .build();
    }


    /**
     * Find all employees with meeting conflicts for the requested time slot.
     *
     * @param requestedStartTime the start time of the requested meeting
     * @param durationMinutes    the duration of the requested meeting in minutes
     * @return List of employees with conflicting meetings
     */
    public List<Employee> findConflictedParticipants(LocalDateTime requestedStartTime, int durationMinutes) {
        List<Employee> conflictingParticipants = new ArrayList<>();
        LocalDateTime requestedEndTime = requestedStartTime.plusMinutes(durationMinutes);

        // Retrieve all meetings that overlap with the requested time slot
        List<Meeting> conflictingMeetings = meetingRepository.findMeetingsInTimeRange(requestedStartTime, requestedEndTime);

        // Extract the participants from these conflicting meetings
        for (Meeting meeting : conflictingMeetings) {
            for (Employee participant : meeting.getParticipants()) {
                // Add the participant if not already in the list to avoid duplicates
                if (!conflictingParticipants.contains(participant)) {
                    conflictingParticipants.add(participant);
                }
            }
        }

        return conflictingParticipants;
    }

    @Transactional(readOnly = true)
    // Check if the requested slot conflicts with any existing meeting
    @Override
    public List<CalendarSlotResponseDTO> getAvailableSlots(List<Long> employeeIds,
                                                           LocalDateTime requestedStartTime,
                                                           int durationMinutes) {
        List<CalendarSlotResponseDTO> availableSlots = new ArrayList<>();

        // Calculate the requested end time based on the requested start time and duration
        LocalDateTime requestedEndTime = requestedStartTime.plusMinutes(durationMinutes);

        // Loop through each employee's calendar slots to check availability
        for (Long employeeId : employeeIds) {
            // Get the employee's calendar slots
            List<CalendarSlot> employeeSlots = calendarSlotRepository.findByEmployeeId(employeeId);

            // For each calendar slot, check if it is available
            for (CalendarSlot slot : employeeSlots) {
                boolean isAvailable = true;

                // Check if the slot is already occupied by a meeting
                List<Meeting> meetings = meetingRepository.findByEmployeeId(employeeId);
                for (Meeting meeting : meetings) {
                    // Check if the meeting times overlap with the requested time slot
                    if (meeting.getStartTime().isBefore(requestedEndTime) && meeting.getEndTime().isAfter(requestedStartTime)) {
                        isAvailable = false; // There is an overlap with an existing meeting
                        break;
                    }
                }

                // If the slot is available, add it to the list
                if (isAvailable) {
                    // Ensure the calendar slot is within the requested time frame
                    if (slot.getStartTime().isAfter(requestedStartTime) && slot.getStartTime().isAfter(requestedEndTime)
                            || slot.getEndTime().isBefore(requestedStartTime) && slot.getEndTime().isBefore(requestedEndTime)) {
                        slot.setAvailable(true);
                        availableSlots.add(CalendarSlotResponseDTO.builder()
                                .id(slot.getId())
                                .employee(EmployeeResponseDTO.builder()
                                        .id(slot.getEmployee().getId())
                                        .name(slot.getEmployee().getName())
                                        .email(slot.getEmployee().getEmail())
                                        .build())
                                .startTime(slot.getStartTime())
                                .endTime(slot.getEndTime())
                                .isAvailable(slot.isAvailable())
                                .build()
                        );
                    }
                }
            }
        }
        return availableSlots;
    }

}
