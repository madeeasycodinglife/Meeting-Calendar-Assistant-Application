package com.madeeasy.service;

import com.madeeasy.dto.request.EmployeeRequestDTO;
import com.madeeasy.dto.response.EmployeeResponseDTO;
import com.madeeasy.entity.Employee;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeRequestDTO);

    EmployeeResponseDTO getEmployeeById(Long id);

    List<EmployeeResponseDTO> getAllEmployees();

    boolean employeeExists(Long id);
}
