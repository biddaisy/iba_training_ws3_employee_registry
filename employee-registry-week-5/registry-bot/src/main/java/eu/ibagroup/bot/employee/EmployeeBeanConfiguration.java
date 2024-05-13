package eu.ibagroup.bot.employee;

import eu.ibagroup.bot.config.EmployeeRegistryBotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class EmployeeBeanConfiguration {

    private static final String X_API_KEY = "x-api-key";

    private final EmployeeRegistryBotConfig config;

    @Bean
    public WebClient getWebClient() {
        return WebClient
                .builder()
                .baseUrl(config.getWebBackendUrl())
                .defaultHeader(X_API_KEY, config.getWebApiKey())
                .build();
    }

    @Bean
    public RestClient getRestClient() {
        return RestClient
                .builder()
                .baseUrl(config.getWebBackendUrl())
                .defaultHeader(X_API_KEY, config.getWebApiKey())
                .build();
    }
}
