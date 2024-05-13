/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import eu.ibagroup.bot.command.Command;
import eu.ibagroup.bot.config.EmployeeRegistryBotConfig;
import eu.ibagroup.common.mongo.collection.Employee;
import eu.ibagroup.common.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;
import java.util.Base64;

/**
 * This command retrieves an Employee details by his PersonUN passed in callback query data
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetEmployeCommand extends BotCommand {

    private final SessionService sessionService;

    private final EmployeeRegistryBotConfig config;

    @Override
    public Command getCommand() {
        return Command.GET_EMPLOYEE;
    }

    private WebClient webClient;

    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = WebClient
                    .builder()
                    .baseUrl(config.getWebBackendUrl())
                    .defaultHeader("x-api-key", config.getWebApiKey())
                    .build();
        }
        return webClient;
    }

    @Override
    public CommandResult execute(Update update) {
        // for this command we ALWAYS must have no Message in Update, but a CallbackQuery instead
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        if (!sessionService.isUserVerified(chatId)) {
            var resTxt = "You need to pass verification to use this command";
            return CommandResult.builder().text(resTxt).build();
        }

        String empId = update.getCallbackQuery().getData();

        Employee emp = getEmployee(empId);

        var details = """
                <b>%s %s</b>
                <b>%s %s %s</b>
                Position: <b>%s</b>
                Company branch: <b>%s</b>
                E-mail: <b>%s</b>
                """.formatted(emp.getLastNameEn(),
                emp.getFirstNameEn(),
                emp.getLastName(),
                emp.getFirstName(),
                emp.getMiddleName(),
                emp.getPosition(),
                emp.getCompanyBranch(),
                emp.getEmail());

        var byteArr = Base64.getDecoder().decode(emp.getPhoto());
        SendPhoto sp = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(new ByteArrayInputStream(byteArr), "photo.jpg"))
                .caption(details)
                .protectContent(true)
                .parseMode(ParseMode.HTML)
                .build();

        return CommandResult.builder().photo(sp).build();
    }

    /**
     * Retrieve the Employee by the Personal ID
     *
     * @param empId (e.g. 100z123)
     * @return Employee
     */
    Employee getEmployee(String empId) {
        String uri = UriComponentsBuilder
                .fromUriString("/employee/" + empId)
                .build().toUriString();
        log.info("Retrieve single Employee by URI {}", uri);
        Employee emp = null;
        try {
            emp = getWebClient().get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
            log.info("Employee retrieved {}", emp);
        } catch (WebClientResponseException wcre) {
            log.error("Error Response Code is {} and Response Body is {}", wcre.getStatusCode(), wcre.getResponseBodyAsString());
            log.error("Exception during employee retrieval", wcre);
        } catch (Exception ex) {
            log.error("Exception during employee retrieval", ex);
        }
        return emp;
    }

    @Override
    public String getDescription() {
        return "Returns the employee details";
    }

    public String toString() {
        return "[Click the button with Employee name]";
    }
}
