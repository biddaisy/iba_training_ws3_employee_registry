/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * The HTTP filter class is responsible for user authentication:
 * - tries to create Authentication object
 * - if succeeds -- assigns it to the SecurityContextHolder
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyAuthenticationExtractor extractor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        extractor.extract(request)
                .ifPresent(
                        SecurityContextHolder.getContext()::setAuthentication
                );
        filterChain.doFilter(request, response);
    }
}
