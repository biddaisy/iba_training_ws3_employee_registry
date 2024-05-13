/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import eu.ibagroup.bot.command.Command;
import eu.ibagroup.bot.employee.EmployeeFacade;
import eu.ibagroup.common.mongo.collection.Employee;
import eu.ibagroup.common.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

/**
 * This command searches Employees by last name (cyrillic or latin letters)
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FindEmployeesBotCommand extends BotCommand {

    private final SessionService sessionService;
    private final EmployeeFacade employeeFacade;

    @Override
    public Command getCommand() {
        return Command.FIND_EMPLOYEES;
    }

    @Override
    public CommandResult execute(Update update) {
        var chatId = update.getMessage().getChatId();

        if (!sessionService.isUserVerified(chatId)) {
            return getUserNotVerifiedCommandResult();
        }

        return findEmployees(update.getMessage().getText());
    }

    private CommandResult findEmployees(String strToSearch) {
        val employees = employeeFacade.findEmployees(strToSearch);
        val markup = getKeyboardMarkup(employees);

        val message = switch (employees.size()) {
            case 0 -> "No Employees were found";
            case 1 -> "One Employee was found:";
            default -> "These Employees were found:";
        };

        return CommandResult.builder().text(message).markup(markup).build();
    }

    /**
     * Prepares an inline keyboard with 1 column of the buttons,
     * a button matches a single Employee and will have his/her name as a caption
     *
     * @param employees of Employees
     * @return inline keyboard for reply markup
     */
    InlineKeyboardMarkup getKeyboardMarkup(List<Employee> employees) {
        return new InlineKeyboardMarkup(getKeyboard(employees));
    }

    private static List<List<InlineKeyboardButton>> getKeyboard(List<Employee> employees) {
        return employees.stream().map(FindEmployeesBotCommand::getButtonRow).toList();
    }

    private static List<InlineKeyboardButton> getButtonRow(Employee employee) {
        return List.of(getButton(employee));
    }

    private static InlineKeyboardButton getButton(Employee employee) {
        val button = new InlineKeyboardButton();
        // ACTIVITY 1 BEGINS
        String title = getTitle(employee);
        button.setText(title); // assign real name (must support latin and cyrillic, depending on search)
        button.setCallbackData(employee.get_id()); // assign Employee ID as callback data
        // ACTIVITY 1 ENDS
        return button;
    }

    private static String getTitle(Employee employee) {
        boolean isTextCyrillic = Strings.isNotEmpty(employee.getFirstName());
        return isTextCyrillic
                ? employee.getFirstName() + " " + employee.getLastName()
                : employee.getFirstNameEn() + " " + employee.getLastNameEn();
    }

    @Override
    public String getDescription() {
        return "Search Employees by the last name beginning (case insensitive, Russian or English)";
    }

    @Override
    public String toString() {
        return "Abcxyz";
    }
}
