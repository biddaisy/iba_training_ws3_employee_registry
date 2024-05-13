/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot  .config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class provides access to properties file
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@ConfigurationProperties("bot")
@Getter
@Setter
@Component
public class EmployeeRegistryBotConfig {

    /**
     * Contains TG bot name picked during bot registration
     */
    private String name;

    /**
     * Contains TG bot access token assigned during bot registration
     */
    private String accessToken;

    /**
     * API Key to access backend web application
     */
    private String webApiKey;

    /**
     * Backend web application address
     */
    private String webBackendUrl;

    private String helpMessage;
}
