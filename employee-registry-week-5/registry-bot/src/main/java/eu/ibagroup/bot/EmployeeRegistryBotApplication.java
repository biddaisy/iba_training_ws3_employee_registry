/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot;

import eu.ibagroup.bot.telegram.EmployeeRegistryBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Main bot application class.
 * Starts Spring Boot application, and registers the Telegram bot.
 * Does not listen HTTP port.
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {"eu.ibagroup.bot", "eu.ibagroup.common"})
@PropertySource(value = {"application-common.properties", "application.properties"})
@EnableMongoRepositories("eu.ibagroup.common") // enable MongoDB repositories located in common module
@RequiredArgsConstructor
public class EmployeeRegistryBotApplication {

    private final EmployeeRegistryBot registryBot;

    public static void main(String[] args) {
        new SpringApplicationBuilder(EmployeeRegistryBotApplication.class)
                .web(WebApplicationType.NONE) // disable default Tomcat server, we don't need to listen HTTP port
                .run(args);
    }

    /**
     * Once Spring Boot application fully started, perform the bot registration
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(registryBot);
            log.info("Employee Registry Telegram Bot successfully registered with the Telegram server");
        } catch (TelegramApiRequestException e) {
            log.error("Not able to register bot", e);
            System.exit(1);
        }
    }

}
