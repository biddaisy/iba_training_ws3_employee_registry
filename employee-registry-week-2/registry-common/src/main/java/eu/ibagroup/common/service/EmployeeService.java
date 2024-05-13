/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.common.service;

import eu.ibagroup.common.mongo.EmployeeRepository;
import eu.ibagroup.common.mongo.collection.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * A Service to work with Employee objects
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService
{
    private final EmployeeRepository employeeRepository;

    public Optional<Employee> findById(String id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> findByNameEn(String pattern, int limit) {
        log.info("Search by latin pattern, last name begins with '{}', limit = {}", pattern, limit);
        return employeeRepository.findEn("^" + pattern, Pageable.ofSize(limit));
    }

    public List<Employee> findTop10ByNameEn(String pattern) {
        log.info("Search 10 by latin pattern, last name begins with '{}'", pattern);
        return employeeRepository.findTop10ByLastNameEnIgnoreCaseStartingWith(pattern);
    }

    public List<Employee> findByName(String pattern, int limit) {
        log.info("Search by cyrillic pattern, last name begins with '{}', limit {}", pattern, limit);
        return employeeRepository.find("^" + pattern, Pageable.ofSize(limit));
    }

    public List<Employee> findTop10ByName(String pattern) {
        log.info("Search 10 by cyrillic pattern, last name begins with '{}'", pattern);
        return employeeRepository.findTop10ByLastNameIgnoreCaseStartingWith(pattern);
    }

    public List<Employee> findEverywhere(String pattern, int limit) {
        return employeeRepository.findEverywhere("^" + pattern, Pageable.ofSize(limit));
    }

    public List<Employee> findAll() {
        return employeeRepository.findAllEmployees();
    }

}
