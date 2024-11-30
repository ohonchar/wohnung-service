package com.ber.wohnung.service.service;

import com.ber.wohnung.service.EmployeeRepo;
import com.ber.wohnung.service.entity.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    @Autowired
    private final EmployeeRepo employeeRepo;

    public List<Employee> getAllEmployees(){
        return employeeRepo.findAll();
    }

    public Employee getEmployeeById(Integer id){
        Optional<Employee> optionalEmployee = employeeRepo.findById(id);
        if(optionalEmployee.isPresent()){
            return optionalEmployee.get();
        }
        log.info("Employee with id: {} doesn't exist", id);
        return null;
    }

    public Employee saveEmployee (Employee employee){
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        Employee savedEmployee = employeeRepo.save(employee);

        log.info("Employee with id: {} saved successfully", employee.getId());
        return savedEmployee;
    }

    public Employee updateEmployee (Employee employee) {
        Optional<Employee> existingEmployee = employeeRepo.findById(employee.getId());
        employee.setCreatedAt(existingEmployee.get().getCreatedAt());
        employee.setUpdatedAt(LocalDateTime.now());

        Employee updatedEmployee = employeeRepo.save(employee);

        log.info("Employee with id: {} updated successfully", employee.getId());
        return updatedEmployee;
    }

    public void deleteEmployeeById (Integer id) {
        employeeRepo.deleteById(id);
    }

}
