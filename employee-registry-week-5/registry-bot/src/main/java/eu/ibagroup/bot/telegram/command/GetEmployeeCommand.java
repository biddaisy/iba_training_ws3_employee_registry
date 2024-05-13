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
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;
import java.util.Base64;

/**
 * This command retrieves an Employee details by his PersonUN passed in callback query data
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetEmployeeCommand extends BotCommand {

    private final SessionService sessionService;
    private final EmployeeFacade employeeFacade;

    @Override
    public Command getCommand() {
        return Command.GET_EMPLOYEE;
    }

    @Override
    public CommandResult execute(Update update) {
        // for this command we ALWAYS must have no Message in Update, but a CallbackQuery instead
        val chatId = update.getCallbackQuery().getMessage().getChatId();
        if (!sessionService.isUserVerified(chatId)) {
            return getUserNotVerifiedCommandResult();
        }

        return getEmployeeCardCommandResult(update, chatId);
    }

    private CommandResult getEmployeeCardCommandResult(Update update, Long chatId) {
        val employeeId = update.getCallbackQuery().getData();
        val employee = employeeFacade.getEmployee(employeeId);

        if (employee == null) {
            return CommandResult.builder().text("Something went wrong").build();
        }

        val employeeDetails = getEmployeeDetails(employee);
        val photoDecoded = Base64.getDecoder().decode(employee.getPhoto());
        val sendPhoto = buildSendPhoto(chatId, photoDecoded, employeeDetails);

        return CommandResult.builder().photo(sendPhoto).build();
    }

    private static SendPhoto buildSendPhoto(Long chatId, byte[] photoDecoded, String employeeDetails) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(new ByteArrayInputStream(photoDecoded), "photo.jpg"))
                .caption(employeeDetails)
                .protectContent(true)
                .parseMode(ParseMode.HTML)
                .build();
    }

    private static String getEmployeeDetails(Employee employee) {
        return """
                <b>%s %s</b>
                <b>%s %s %s</b>
                Position: <b>%s</b>
                Company branch: <b>%s</b>
                E-mail: <b>%s</b>
                """
                .formatted(
                        employee.getLastNameEn(),
                        employee.getFirstNameEn(),
                        employee.getLastName(),
                        employee.getFirstName(),
                        employee.getMiddleName(),
                        employee.getPosition(),
                        employee.getCompanyBranch(),
                        employee.getEmail()
                );
    }

    @Override
    public String getDescription() {
        return "Returns the employee details";
    }

    @Override
    public String toString() {
        return "[Click the button with Employee name]";
    }
}
