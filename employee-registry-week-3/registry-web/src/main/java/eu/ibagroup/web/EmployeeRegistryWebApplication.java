/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Web Application entry point class.
 * <p>
 * - Defines which packages Spring should scan in order to perform dependency injections.
 * - Defines location of property files.
 * - Enables MongoDB repositories.
 * - Adds new file as properties source
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@SpringBootApplication(scanBasePackages = {"eu.ibagroup.web", "eu.ibagroup.common"} , exclude = { SecurityAutoConfiguration.class })
@PropertySource(
        value = {"application-common.properties", "application.properties", "classpath:git.properties"},
        ignoreResourceNotFound = true
)
@EnableMongoRepositories("eu.ibagroup.common") // enable MongoDB repositories located in common module
public class EmployeeRegistryWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeRegistryWebApplication.class, args);
    }

}
