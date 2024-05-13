package eu.ibagroup.bot.employee;

import eu.ibagroup.bot.config.EmployeeRegistryBotConfig;
import eu.ibagroup.common.mongo.collection.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
@Slf4j
@RequiredArgsConstructor
@Qualifier("webEmployeeClient")
public class WebEmployeeClient implements EmployeeClient {

    private final EmployeeRegistryBotConfig config;

    private final WebClient webClient;

    public List<Employee> getEmployees(String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(Employee.class)
                .collectList()
                .doOnError(WebClientResponseException.class, this::logError)
                .doOnError(Exception.class, ex -> log.error(EXCEPTION_DURING_EMPLOYEES_SEARCH, ex))
                .onErrorResume(ex -> Mono.just(emptyList()))
                .block();
    }

    public Employee getEmployee(String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Employee.class)
                .doOnError(WebClientResponseException.class, this::logError)
                .doOnError(Exception.class, ex -> log.error(EXCEPTION_DURING_EMPLOYEE_RETRIEVAL, ex))
                .onErrorResume(ex -> Mono.empty())
                .block();
    }

    private void logError(WebClientResponseException webClientResponseException) {
        log.error(
                "Error Response Code is {} and Response Body is {}",
                webClientResponseException.getStatusCode(),
                webClientResponseException.getResponseBodyAsString()
        );
    }

}
