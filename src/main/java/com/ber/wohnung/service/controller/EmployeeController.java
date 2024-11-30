package com.ber.wohnung.service.controller;

import com.ber.wohnung.service.service.selenium.RunUiService;
import com.ber.wohnung.service.entity.Employee;
import com.ber.wohnung.service.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee/v1")
@RequiredArgsConstructor
@Validated
public class EmployeeController {

    @Autowired
    private final EmployeeService employeeService;

    @Autowired
    private final RunUiService runUiService;

    /**
     * This method is called when a GET request is made
     * URL: localhost:8080/employee/v1/
     * Purpose: Fetches all the employees in the employee table
     * @return List of Employees
     */
    @GetMapping("/")
    public ResponseEntity<List<Employee>> getAllEmployees(){
        return ResponseEntity.ok().body(employeeService.getAllEmployees());
    }

    /**
     * This method is called when a GET request is made
     * URL: localhost:8080/employee/v1/1 (or any other id)
     * Purpose: Fetches employee with the given id
     * @param id - employee id
     * @return Employee with the given id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Integer id)
    {
        return ResponseEntity.ok().body(employeeService.getEmployeeById(id));
    }

    /**
     * This method is called when a POST request is made
     * URL: localhost:8080/employee/v1/
     * Purpose: Save an Employee entity
     * @param employee - Request body is an Employee entity
     * @return Saved Employee entity
     */
    @PostMapping("/")
    public ResponseEntity<Employee> saveEmployee(@RequestBody Employee employee)
    {
        return ResponseEntity.ok().body(employeeService.saveEmployee(employee));
    }

    /**
     * This method is called when a PUT request is made
     * URL: localhost:8080/employee/v1/
     * Purpose: Update an Employee entity
     * @param employee - Employee entity to be updated
     * @return Updated Employee
     */
    @PutMapping("/")
    public ResponseEntity<Employee> updateEmployee(@RequestBody Employee employee)
    {
        return ResponseEntity.ok().body(employeeService.updateEmployee(employee));
    }

    /**
     * This method is called when a PUT request is made
     * URL: localhost:8080/employee/v1/1 (or any other id)
     * Purpose: Delete an Employee entity
     * @param id - employee's id to be deleted
     * @return a String message indicating employee record has been deleted successfully
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable Integer id)
    {
        employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok().body("Deleted employee successfully");
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<String> executeUi() {
        runUiService.executeUi();
        return ResponseEntity.ok().body("UI script finished");
    }


}
