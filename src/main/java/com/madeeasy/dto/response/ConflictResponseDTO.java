package com.madeeasy.dto.response;


import com.madeeasy.entity.Employee;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConflictResponseDTO {
    private List<Employee> conflictedEmployees;
}
