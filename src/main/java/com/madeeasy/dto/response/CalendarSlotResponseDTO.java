package com.madeeasy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarSlotResponseDTO {

    private Long id;
    private EmployeeResponseDTO employee;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isAvailable;
}
