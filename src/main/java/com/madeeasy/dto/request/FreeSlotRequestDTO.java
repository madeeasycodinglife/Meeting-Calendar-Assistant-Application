package com.madeeasy.dto.request;

import lombok.Data;

import java.time.Duration;
import java.util.List;

@Data
public class FreeSlotRequestDTO {
    private List<Long> employeeIds;
    private Duration duration;
}
