package eu.ibagroup.bot.employee;

import eu.ibagroup.common.mongo.collection.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
@Slf4j
@RequiredArgsConstructor
@Qualifier("restEmployeeClient")
public class RestEmployeeClient implements EmployeeClient {

    private final RestClient restClient;

    @Override
    public List<Employee> getEmployees(String uri) {
        try {
            return restClient.get()
                    .uri(uri)
                    .retrieve().body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception ex) {
            log.error(EXCEPTION_DURING_EMPLOYEES_SEARCH, ex);
        }
        return emptyList();
    }

    @Override
    public Employee getEmployee(String uri) {
        try {
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(Employee.class);
        } catch (Exception ex) {
            log.error(EXCEPTION_DURING_EMPLOYEE_RETRIEVAL, ex);
        }
        return null;
    }

}
