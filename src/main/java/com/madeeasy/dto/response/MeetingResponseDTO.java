package com.madeeasy.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MeetingResponseDTO {
    private Long id;
    private String topic;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<EmployeeResponseDTO> participants;
}
