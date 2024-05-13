/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.web.controller;

import eu.ibagroup.common.mongo.collection.Employee;
import eu.ibagroup.common.service.EmployeeService;
import eu.ibagroup.web.exception.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

/**
 * REST controller to work with Employees
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
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
    List<Employee> getEmployees(
            @RequestParam(name = "q", required = false) String pattern,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit
    ) {
        if (isBlank(pattern)) return employeeService.findAll();

        // ACTIVITY 1. Check if patterns is cyrillic (i.e. contains a cyrillic character)
        // and if yes, call cyrillic finder in the service
        val hasCyrillic = hasCyrillic(pattern);

        if (limit == 10) {
            return hasCyrillic ? employeeService.findTop10ByName(pattern) : employeeService.findTop10ByNameEn(pattern);
        } else {
            return hasCyrillic ? employeeService.findByName(pattern, limit) : employeeService.findByNameEn(pattern, limit);
        }
    }

    @GetMapping("/employees")
    List<Employee> getEmployeesEverywhere(
            @RequestParam(name = "q", required = false) String pattern,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit
    ) {
        if (isBlank(pattern)) return employeeService.findAll();

        return employeeService.findEverywhere(pattern, limit);
    }

    private static boolean isBlank(String pattern) {
        return (pattern == null) || pattern.isBlank();
    }

    private static boolean hasCyrillic(String pattern) {
        return Pattern.matches(".*\\p{InCyrillic}.*", pattern);
    }

}
