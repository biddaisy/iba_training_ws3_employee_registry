/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.web.config;

import eu.ibagroup.web.controller.EmployeeController;
import eu.ibagroup.web.security.ApiKeyAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * Web Application security configuration
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class RegistryWebApplicationConfig {

    private static final String API_KEY_IS_MISSING = "API Key is missing";

    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityMatcher("/**")
                .authorizeHttpRequests(RegistryWebApplicationConfig::getAuthenticated)
                .exceptionHandling(RegistryWebApplicationConfig::getApiKeyIsMissing)
                .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private static void getApiKeyIsMissing(final ExceptionHandlingConfigurer<HttpSecurity> configurer) {
        configurer.authenticationEntryPoint(RegistryWebApplicationConfig::authenticationEntryPoint);
    }

    private static void authenticationEntryPoint(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(API_KEY_IS_MISSING);
    }

    private static void getAuthenticated(
            final AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
                // ACTIVITY 3. Allow /version unauthenticated requests
                .requestMatchers(EmployeeController.VERSION_URL).permitAll()
                .anyRequest().authenticated();
    }
}
