package com.madeeasy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotResponseDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}
