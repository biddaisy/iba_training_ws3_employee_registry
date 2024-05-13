/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.web.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * The class is responsible for user authentication:
 * - retrieves API key HTTP header from HTTP request
 * - check if the header value is valid
 * - in case of success returns Authentication instance wrapped in Optional
 * - otherwise returns empty Optional
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Component
public class ApiKeyAuthenticationExtractor {

    @Value("${application.security.api-keys}")
    private Set<String> apiKeys; // optionally can be loaded from a database

    public Optional<Authentication> extract(HttpServletRequest request) {
        String providedKey = request.getHeader("x-api-key");
        if (providedKey == null || !apiKeys.contains(providedKey))
            return Optional.empty();

        return Optional.of(new ApiKeyAuthentication(providedKey, AuthorityUtils.NO_AUTHORITIES));
    }

}
