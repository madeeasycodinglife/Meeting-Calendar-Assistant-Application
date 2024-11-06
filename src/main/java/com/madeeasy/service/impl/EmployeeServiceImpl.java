package com.madeeasy.service.impl;

import com.madeeasy.dto.request.EmployeeRequestDTO;
import com.madeeasy.dto.response.EmployeeResponseDTO;
import com.madeeasy.entity.Employee;
import com.madeeasy.exception.ConflictException;
import com.madeeasy.repository.EmployeeRepository;
import com.madeeasy.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO employee) {

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new ConflictException("Employee with Email " + employee.getEmail() + " already exists");
        }

        Employee newEmployee = Employee.builder()
                .name(employee.getName())
                .email(employee.getEmail())
                .build();

        Employee savedEmployee = this.employeeRepository.save(newEmployee);

        return EmployeeResponseDTO.builder()
                .id(savedEmployee.getId())
                .name(savedEmployee.getName())
                .email(savedEmployee.getEmail())
                .build();
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee foundEmployeeById = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));

        return EmployeeResponseDTO.builder()
                .id(foundEmployeeById.getId())
                .name(foundEmployeeById.getName())
                .email(foundEmployeeById.getEmail())
                .build();
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {
        List<Employee> employeeList = employeeRepository.findAll();

        return employeeList.stream()
                .map(employee -> EmployeeResponseDTO.builder()
                        .id(employee.getId())
                        .name(employee.getName())
                        .email(employee.getEmail())
                        .build())
                .toList();
    }

    @Override
    public boolean employeeExists(Long id) {
        return employeeRepository.existsById(id);
    }
}
