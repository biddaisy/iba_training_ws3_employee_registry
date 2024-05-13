/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import eu.ibagroup.bot.command.Command;
import eu.ibagroup.bot.config.EmployeeRegistryBotConfig;
import eu.ibagroup.common.mongo.collection.Employee;
import eu.ibagroup.common.service.SessionService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * This command searches Employees by last name (cyrillic or latin letters)
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FindEmployeesBotCommand extends BotCommand {

    private final SessionService sessionService;

    private final EmployeeRegistryBotConfig config;

    @Override
    public Command getCommand() {
        return Command.FIND_EMPLOYEES;
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
        var chatId = update.getMessage().getChatId();
        InlineKeyboardMarkup ikm = null;
        String resTxt;
        if (!sessionService.isUserVerified(chatId)) {
            resTxt = "You need to pass verification to use this command";
        } else {
            String strToSearch = update.getMessage().getText();
            List<Employee> list = getListOfEmployees(strToSearch);
            ikm = getKeyboardMarkup(list);
            resTxt = switch (list.size()) {
                case 0 -> "No Employees were found";
                case 1 -> "One Employee was found:";
                default -> "These Employees were found:";
            };
        }
        return CommandResult.builder().text(resTxt).markup(ikm).build();
    }

    List<Employee> getListOfEmployees(String strToSearch) {
        String uri= UriComponentsBuilder
                .fromUriString("/employee")
                .queryParam("q", strToSearch)
                .build().toUriString();
        log.info("Find Employees by URI {}", uri);
        List<Employee> list = List.of();
        try {
            list =  getWebClient().get()
                    .uri(uri)
                    .retrieve()
                    .bodyToFlux(Employee.class)
                    .collectList()
                    .block();
            log.info("Employees found {}", list);
        } catch (WebClientResponseException wcre) {
            log.error("Error Response Code is {} and Response Body is {}" ,wcre.getStatusCode(), wcre.getResponseBodyAsString());
            log.error("Exception during Employees search", wcre);
        } catch (Exception ex) {
            log.error("Exception during Employees search",ex);
        }
        return list;
    }

    /**
     * Prepares an inline keyboard with 1 column of the buttons,
     * a button matches a single Employee and will have his/her name as a caption
     *
     * @param list of Employees
     * @return inline keyboard for reply markup
     */
    InlineKeyboardMarkup getKeyboardMarkup(List<Employee> list) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Employee employee : list) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            if (employee.getLastNameEn() != null) { // use English or Cyrillic name
                inlineKeyboardButton.setText(employee.getLastNameEn() + " " + employee.getFirstNameEn());
            } else {
                inlineKeyboardButton.setText(employee.getLastName() + " " +  employee.getFirstName() + " " + employee.getMiddleName());
            }
            inlineKeyboardButton.setCallbackData(employee.get_id());
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(inlineKeyboardButton);
            rowList.add(keyboardButtonsRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    @Override
    public String getDescription() {
        return "Search Employees by the last name beginning (case insensitive, Russian or English)";
    }

    public String toString() {
        return "Abcxyz";
    }
}
