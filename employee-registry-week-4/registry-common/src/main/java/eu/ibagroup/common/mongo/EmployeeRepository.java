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

    @Query(value="{'lastNameEn' : {$regex : ?0, $options: 'i'}}", fields="{'firstNameEn' : 1, 'lastNameEn' : 1}")
    List<Employee> findEn(String pattern, Pageable pageable);

    @Query(value="{'lastName' : {$regex : ?0, $options: 'i'}}", fields="{'firstName' : 1, 'lastName' : 1}")
    List<Employee> findRu(String pattern, Pageable pageable);

    @Query(value="{'phoneHash' : {$regex : ?0}}", fields="{'firstNameEn' : 1, 'lastNameEn' : 1}")
    List<Employee> findByPhone(String pattern);

}
