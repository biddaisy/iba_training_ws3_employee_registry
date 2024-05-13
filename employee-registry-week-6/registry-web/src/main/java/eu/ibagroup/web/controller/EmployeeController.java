/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.web.controller;

import eu.ibagroup.common.mongo.collection.Employee;
import eu.ibagroup.common.service.EmployeeService;
import eu.ibagroup.web.exception.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller to work with Employees
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/employee/{id}")
    Employee getEmployee(@PathVariable String id) {
        return employeeService.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @GetMapping("/employee")
    List<Employee> getEmployees(@RequestParam(name = "q") String pattern) {
        if ((pattern == null) || pattern.isBlank()) { return List.of(); }

        if (Character.UnicodeBlock.of(pattern.charAt(0)).equals(Character.UnicodeBlock.CYRILLIC)) {
            return employeeService.findByNameRu(pattern);
        }

        return employeeService.findByNameEn(pattern);
    }
}
