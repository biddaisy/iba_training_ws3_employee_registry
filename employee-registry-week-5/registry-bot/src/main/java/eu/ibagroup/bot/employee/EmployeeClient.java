package eu.ibagroup.bot.employee;

import eu.ibagroup.common.mongo.collection.Employee;

import java.util.List;

public interface EmployeeClient {

    String EXCEPTION_DURING_EMPLOYEES_SEARCH = "Exception occurred during Employees search";
    String EXCEPTION_DURING_EMPLOYEE_RETRIEVAL = "Exception occurred during employee retrieval";

    List<Employee> getEmployees(String uri);

    Employee getEmployee(String uri);
}
