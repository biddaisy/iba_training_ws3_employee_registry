/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.common.mongo;

import eu.ibagroup.common.mongo.collection.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * MongoDB repository interface, provides custom database operations to work with Employees
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    @Query(value="{'lastNameEn' : {$regex : ?0, $options: 'i'}}", fields="{'firstNameEn' : 1, 'lastNameEn' : 1}, limit = 2")
    List<Employee> findEn(String pattern, Pageable pageable);

    @Query(value="{'lastName' : {$regex : ?0, $options: 'i'}}", fields="{'firstName' : 1, 'middleName' : 1, 'lastName' : 1}")
    List<Employee> find(String pattern, Pageable pageable);

    @Query(fields="{'firstNameEn' : 1, 'lastNameEn' : 1}")
    List<Employee> findTop10ByLastNameEnIgnoreCaseStartingWith(String pattern);

    @Query(fields="{'firstName' : 1, 'middleName' : 1, 'lastName' : 1}")
    List<Employee> findTop10ByLastNameIgnoreCaseStartingWith(String pattern);

    @Query(value="{'$or':[ {'lastName' : {$regex : ?0, $options: 'i'}}, {'lastNameEn' : {$regex : ?0, $options: 'i'}} ] }",
            fields="{'firstName' : 1, 'middleName' : 1, 'lastName' : 1, 'firstNameEn' : 1, 'lastNameEn' : 1}")
    List<Employee> findEverywhere(String pattern, Pageable pageable);

    @Query(value="{'lastName' : {$regex : '.'}}", fields="{'firstName' : 1, 'middleName' : 1, 'lastName' : 1, 'firstNameEn' : 1, 'lastNameEn' : 1}")
    List<Employee> findAllEmployees();

}
