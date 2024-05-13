/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.web.controller;

import eu.ibagroup.common.mongo.collection.Employee;
import eu.ibagroup.common.service.EmployeeService;
import eu.ibagroup.web.exception.EmployeeNotFoundException;
import eu.ibagroup.web.git.GitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller to work with Employees
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@RestController
@RequiredArgsConstructor
public class EmployeeController {

    public static final String VERSION_URL = "/version";

    private final EmployeeService employeeService;
    private final GitService gitService;

    @GetMapping("/employee/{id}")
    Employee getEmployee(@PathVariable String id) {
        return employeeService.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @GetMapping("/employee")
    List<Employee> getEmployees(@RequestParam(name = "q") String pattern) {
        if (isBlank(pattern)) { return List.of(); }

        if(isCyrillic(pattern)) {
            return employeeService.findByNameRu(pattern);
        }

        return employeeService.findByNameEn(pattern);
    }

    private static boolean isCyrillic(String pattern) {
        return Character.UnicodeBlock.of(pattern.charAt(0)).equals(Character.UnicodeBlock.CYRILLIC);
    }

    private static boolean isBlank(String pattern) {
        return (pattern == null) || pattern.isBlank();
    }

    @GetMapping(VERSION_URL)
    Map<String, String> getVersion() {
        return gitService.getVersion();
    }

}
