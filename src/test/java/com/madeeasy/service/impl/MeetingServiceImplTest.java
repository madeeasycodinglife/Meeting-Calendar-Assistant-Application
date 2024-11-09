package com.madeeasy.service.impl;

import com.madeeasy.dto.request.MeetingRequestDTO;
import com.madeeasy.dto.response.CalendarSlotResponseDTO;
import com.madeeasy.dto.response.MeetingResponseDTO;
import com.madeeasy.entity.CalendarSlot;
import com.madeeasy.entity.Employee;
import com.madeeasy.entity.Meeting;
import com.madeeasy.exception.ResourceNotFoundException;
import com.madeeasy.repository.CalendarSlotRepository;
import com.madeeasy.repository.EmployeeRepository;
import com.madeeasy.repository.MeetingRepository;
import com.madeeasy.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingServiceImplTest {

    @InjectMocks
    private MeetingServiceImpl meetingService;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CalendarSlotRepository calendarSlotRepository;

    @Mock
    private EmployeeService employeeService;

    private MeetingRequestDTO meetingRequestDTO;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.now().plusDays(1);
        endTime = startTime.plusHours(1);

        meetingRequestDTO = MeetingRequestDTO.builder()
                .topic("Project Discussion")
                .adminId(1L)
                .participantIds(List.of(2L, 3L))
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    @Test
    void bookMeeting_AdminNotFound_ThrowsException() {
        when(employeeService.employeeExists(meetingRequestDTO.getAdminId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> meetingService.bookMeeting(meetingRequestDTO));
    }

    @Test
    void bookMeeting_ParticipantNotFound_ThrowsException() {
        when(employeeService.employeeExists(meetingRequestDTO.getAdminId())).thenReturn(true);
        when(employeeService.employeeExists(2L)).thenReturn(true);
        when(employeeService.employeeExists(3L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> meetingService.bookMeeting(meetingRequestDTO));
        assertTrue(exception.getMessage().contains("Employees not found with IDs"));
    }

    @Test
    void bookMeeting_ParticipantHasSchedulingConflict_ThrowsResourceNotFoundException() {
        Employee participant = new Employee();
        participant.setId(2L);

        // Only stub the methods that are necessary
        lenient().when(employeeService.employeeExists(anyLong())).thenReturn(true);
        when(employeeRepository.findAllById(anyList())).thenReturn(List.of(participant));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> meetingService.bookMeeting(meetingRequestDTO));
    }


    @Test
    void bookMeeting_ValidRequest_SavesMeeting() {
        // Prepare test data
        Employee admin = new Employee();
        admin.setId(1L);
        admin.setName("Admin Name");
        admin.setEmail("admin@example.com");

        Employee participant1 = new Employee();
        participant1.setId(2L);
        participant1.setName("Participant 1");
        participant1.setEmail("participant1@example.com");

        Employee participant2 = new Employee();
        participant2.setId(3L);
        participant2.setName("Participant 2");
        participant2.setEmail("participant2@example.com");

        Meeting savedMeeting = new Meeting();
        savedMeeting.setId(1L);
        savedMeeting.setTopic("Project Discussion");
        savedMeeting.setStartTime(startTime);
        savedMeeting.setEndTime(endTime);

        // Mocking service methods
        when(employeeService.employeeExists(anyLong())).thenReturn(true); // Mock for employeeExists
        when(employeeRepository.findAllById(anyList())).thenReturn(List.of(participant1, participant2)); // Mock findAllById
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(admin)); // Mock for finding admin by ID
        when(calendarSlotRepository.existsByEmployeeAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(any(), any(), any())).thenReturn(false); // No conflicts
        when(meetingRepository.save(any(Meeting.class))).thenReturn(savedMeeting); // Mock saving the meeting

        // Execute the service method
        MeetingResponseDTO response = meetingService.bookMeeting(meetingRequestDTO);

        // Assertions
        assertNotNull(response, "Response should not be null");
        assertEquals(savedMeeting.getId(), response.getId(), "Meeting ID should match");
        assertEquals(savedMeeting.getTopic(), response.getTopic(), "Meeting topic should match");
    }


    @Test
    void findConflictedParticipants_WithConflicts_ReturnsConflictedEmployees() {
        LocalDateTime requestedStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime requestedEndTime = requestedStartTime.plusMinutes(60);
        Employee participant = new Employee();
        participant.setId(2L);

        Meeting conflictingMeeting = new Meeting();
        conflictingMeeting.setParticipants(List.of(participant));

        when(meetingRepository.findMeetingsInTimeRange(requestedStartTime, requestedEndTime))
                .thenReturn(List.of(conflictingMeeting));

        List<Employee> conflictedParticipants = meetingService.findConflictedParticipants(requestedStartTime, 60);

        assertEquals(1, conflictedParticipants.size());
        assertEquals(participant.getId(), conflictedParticipants.get(0).getId());
    }


    @Test
    void getAvailableSlots_WithAvailableSlot_ReturnsAvailableSlots() {
        // Prepare an available calendar slot for testing
        CalendarSlot availableSlot = new CalendarSlot();
        availableSlot.setId(1L);
        availableSlot.setStartTime(startTime);
        availableSlot.setEndTime(endTime);
        availableSlot.setAvailable(true);

        // Create an Employee object and associate it with the CalendarSlot
        Employee employee = new Employee();
        employee.setId(1L);
        availableSlot.setEmployee(employee);  // Set the Employee on the CalendarSlot

        // No meetings scheduled for the employee, so the slot should be available
        when(calendarSlotRepository.findByEmployeeId(anyLong())).thenReturn(List.of(availableSlot));
        when(meetingRepository.findByEmployeeId(anyLong())).thenReturn(List.of());

        List<CalendarSlotResponseDTO> availableSlots = meetingService.getAvailableSlots(
                List.of(1L),
                LocalDateTime.of(LocalDate.of(2023, 1, 1), startTime.toLocalTime()),
                60
        );

        assertEquals(1, availableSlots.size(), "Expected one available slot");
        assertTrue(availableSlots.get(0).isAvailable(), "The slot should be marked as available");
    }



    @Test
    void getAvailableSlots_WithConflictingMeeting_ReturnsEmptyList() {
        Employee participant = new Employee();
        participant.setId(2L);

        CalendarSlot slot = new CalendarSlot();
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);

        Meeting conflictingMeeting = new Meeting();
        conflictingMeeting.setStartTime(startTime.minusMinutes(30));
        conflictingMeeting.setEndTime(endTime.plusMinutes(30));

        when(calendarSlotRepository.findByEmployeeId(2L)).thenReturn(List.of(slot));
        when(meetingRepository.findByEmployeeId(2L)).thenReturn(List.of(conflictingMeeting));

        List<CalendarSlotResponseDTO> availableSlots = meetingService.getAvailableSlots(List.of(2L), startTime, 60);

        assertTrue(availableSlots.isEmpty());
    }
}
