package com.madeeasy.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class MeetingRequestDTO {
    private Long adminId;
    private String topic;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Long> participantIds;
}
