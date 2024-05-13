/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.common.service;

import eu.ibagroup.common.mongo.EmployeeRepository;
import eu.ibagroup.common.mongo.collection.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
public class EmployeeService {
    private final EmployeeRepository employeeRepo;

    public Optional<Employee> findById(String id) {
        return employeeRepo.findById(id);
    }

    public List<Employee> findByNameRu(String pattern) {
        log.info("Search by cyrillic pattern, last name begins with '{}'", pattern);
        return employeeRepo.findRu("^" + pattern,  PageRequest.of(0, 10));
    }

    public List<Employee> findByNameEn(String pattern) {
        log.info("Search by latin pattern, last name begins with '{}'", pattern);
        return employeeRepo.findEn("^" + pattern, PageRequest.of(0, 10));
    }
}
