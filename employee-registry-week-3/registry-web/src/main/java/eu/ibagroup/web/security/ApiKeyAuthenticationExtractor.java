/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.web.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.val;
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
@RequiredArgsConstructor
public class ApiKeyAuthenticationExtractor {
    private static final String X_API_KEY_HEADER = "x-api-key";

    // ACTIVITY 1. Read API key value/values from properties

    @Value("${application.security.api-keys}")
    private final Set<String> apiKeys;

    public Optional<Authentication> extract(HttpServletRequest request) {
        val providedKey = request.getHeader(X_API_KEY_HEADER); // ACTIVITY 1. Retrieve API key value from HTTP header (if any)
        // ACTIVITY 1. Check if API key exists in HTTP request and matches what configured in properties, then returns value from below, otherwise returns empty Optional
        val isValidKey = apiKeys.contains(providedKey);
        return getAuthentication(isValidKey, providedKey);
    }

    private static Optional<Authentication> getAuthentication(boolean isValidKey, String providedKey) {
        return isValidKey
                ? Optional.of(new ApiKeyAuthentication(providedKey, AuthorityUtils.NO_AUTHORITIES))
                : Optional.empty();
    }
}
