/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram;

import eu.ibagroup.bot.command.Command;
import eu.ibagroup.bot.config.EmployeeRegistryBotCommandFactory;
import eu.ibagroup.bot.employee.EmployeeFacade;
import eu.ibagroup.bot.telegram.command.CommandResult;
import eu.ibagroup.common.mongo.collection.Employee;
import eu.ibagroup.common.mongo.collection.Session;
import eu.ibagroup.common.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * The bot facade class
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class EmployeeRegistryBotFacade {

    private static final String NO_EMPLOYEE_MESSAGE = "There is no employee who provided this number in DB. Please try to authenticate from a different number";
    private static final String MANY_EMPLOYEE_MESSAGE = "There are several employees who provided this number in DB, please try to authenticate from a different unique number";
    private static final String WELCOME_MESSAGE = "Welcome %s %s !%nYou are now eligible to search employees and browse employee info.";

    private final EmployeeRegistryBotCommandFactory commandFactory;
    private final SessionService sessionService;
    private final EmployeeFacade employeeFacade;

    protected CommandResult onUpdateReceived(Update update) {

        return authenticateIfContactPassed(update)
                .orElseGet(() -> initSessionIfNew(update)
                        .orElseGet(() -> executeBotCommand(update)));
    }

    private CommandResult executeBotCommand(Update update) {
        return commandFactory.getBotCommand(update).execute(update);
    }

    private Optional<CommandResult> initSessionIfNew(Update update) {
        return Optional.ofNullable(update.getMessage())
                .map(m -> initSessionIfNew(update, m));

    }

    private CommandResult initSessionIfNew(Update update, Message message) {
        // some text was sent by user to the bot
        // retrieve chat ID
        val chatId = message.getChatId();
        // check if conversation has started, if not -- start conversation
        if (sessionService.isChatNew(chatId)) {
            sessionService.initChat(chatId);
            return commandFactory.getBotCommand(Command.START).execute(update);
        }
        return null;
    }

    private Optional<CommandResult> authenticateIfContactPassed(Update update) {
        // try to authenticate user if s/he passed contact information, it may happen on any stage
        return Optional.ofNullable(update.getMessage())
                .map(Message::getContact)
                .map(c -> authenticateIfPhonePassed(update.getMessage().getChatId(), c));
    }

    private CommandResult authenticateIfPhonePassed(long chatId, Contact contact) {
        return Optional.ofNullable(contact.getPhoneNumber())
                .map(p -> authenticate(chatId, p))
                .orElse(null);

    }

    private CommandResult authenticate(long chatId, String phone) {
        val phoneHash = getPhoneHash(phone);
        val employees = employeeFacade.getEmployeesByPhone(phoneHash);
        var messageText = validateEmployees(chatId, employees, phoneHash);
        return CommandResult.builder().text(messageText).build();
    }

    private String validateEmployees(long chatId, List<Employee> employees, String phoneHash) {
        return switch (employees.size()) {
            case 0 -> NO_EMPLOYEE_MESSAGE;
            case 1 -> updateSession(chatId, phoneHash, employees.get(0));
            default -> MANY_EMPLOYEE_MESSAGE;
        };
    }

    private static String getPhoneHash(String phone) {
        // cut the '+' sign prepended by Telegram, if it presents
        phone = phone.replaceAll("^[^\\d]", "");
        return DigestUtils.sha256Hex(phone);
    }

    private String updateSession(long chatId, String phoneHash, Employee employee) {
        return Optional.ofNullable(sessionService.getSession(chatId))
                .map(s -> updateSession(phoneHash, employee, s))
                .orElse(null);
    }

    private String updateSession(String phoneHash, Employee employee, Session session) {
        session.setContactVerified(true);
        session.setVerifiedDate(LocalDateTime.now());
        session.setPhoneHash(phoneHash);
        sessionService.persist(session);
        return WELCOME_MESSAGE.formatted(employee.getFirstNameEn(), employee.getLastNameEn());
    }
}
