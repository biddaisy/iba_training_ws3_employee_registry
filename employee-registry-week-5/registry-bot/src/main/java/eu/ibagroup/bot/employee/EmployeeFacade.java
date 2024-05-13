package eu.ibagroup.bot.employee;

import eu.ibagroup.common.mongo.collection.Employee;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Getter
@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeFacade {

    private final ApplicationContext applicationContext;
    @Value("${bot.employee-client:restEmployeeClient}")
    private final String employeeClientBeanName;

    private EmployeeClient employeeClient;

    @PostConstruct
    void init() {
        employeeClient = (EmployeeClient) applicationContext.getBean(employeeClientBeanName);
    }

    public List<Employee> findEmployees(String strToSearch) {
        val uri = getFindEmployeesUri(strToSearch);
        return employeeClient.getEmployees(uri);
    }

    public List<Employee> getEmployeesByPhone(String phoneHash) {
        val uri = getFindEmployeesByPhoneUri(phoneHash);
        return employeeClient.getEmployees(uri);
    }

    /**
     * Retrieve the Employee by the Personal ID
     *
     * @param empId (e.g. 100z123)
     * @return
     */
    public Employee getEmployee(String empId) {
        String uri = getEmployeeUri(empId);
        return employeeClient.getEmployee(uri);
    }

    private static String getEmployeeUri(String employeeId) {
        return UriComponentsBuilder
                .fromUriString("/employee/" + employeeId)
                .build().toUriString();
    }

    private static String getFindEmployeesUri(String strToSearch) {
        return UriComponentsBuilder
                .fromUriString("/employee")
                .queryParam("q", strToSearch)
                .build().toUriString();
    }

    private static String getFindEmployeesByPhoneUri(String phoneHash) {
        return UriComponentsBuilder
                .fromUriString("/employee")
                .queryParam("p", phoneHash)
                .build().toUriString();
    }

}
