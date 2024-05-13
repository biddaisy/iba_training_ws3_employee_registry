/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram;

import eu.ibagroup.bot.config.EmployeeRegistryBotCommandFactory;
import eu.ibagroup.bot.telegram.command.BotCommand;
import eu.ibagroup.bot.telegram.command.Command;
import eu.ibagroup.common.mongo.collection.Employee;
import eu.ibagroup.common.mongo.collection.Session;
import eu.ibagroup.common.service.EmployeeService;
import eu.ibagroup.common.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static eu.ibagroup.bot.telegram.EmployeeRegistryBot.getChatId;
import static eu.ibagroup.bot.telegram.EmployeeRegistryBot.getInputMessage;

/**
 * The bot facade class
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class EmployeeRegistryBotFacade {

    private static final String FOUND_MANY_EMPLOYEE_MESSAGE = "There are several employees who provided this number in DB, please try to authenticate from a different unique number";
    private static final String NOT_FOUND_EMPLOYEE_MESSAGE = "There is no employee who provided this number in DB. Please try to authenticate from a different number";
    private static final String AUTHENTICATED_MESSAGE = "Welcome %s %s !%nYou are now eligible to search employees and browse employee info.";

    private final EmployeeRegistryBotCommandFactory commandFactory;

    private final SessionService sessionService;

    private final EmployeeService employeeService;

    protected String onUpdateReceived(Update update) {

        // try to authenticate user if s/he passed contact information, it may happen on any stage
        return authenticateIfContactPassed(update)
                .orElseGet(() -> execute(update));
    }

    private String execute(Update update) {
        if (sessionService.initIfNewChat(getChatId(update))) {
            return commandFactory.getBotCommand(Command.START).execute(update);
        }
        return getBotCommand(update).execute(update);
    }

    private BotCommand getBotCommand(Update update) {
        val inputMessage = getInputMessage(update);
        return commandFactory.getBotCommand(inputMessage);
    }

    private Optional<String> authenticateIfContactPassed(Update update) {
        String phone = getPhone(getContact(update));
        if (Strings.isNotEmpty(phone)) {
            val session = getSession(getChatId(update));
            return Optional.of(authenticate(session, phone));
        }
        return Optional.empty();
    }

    private static String getPhone(Contact contact) {
        return contact != null ? contact.getPhoneNumber() : null;
    }

    private static Contact getContact(Update update) {
        return update.getMessage().getContact();
    }

    private String authenticate(Session session, String rawPhone) {
        // ACTIVITY 2: delete leading + sign from the phone (if it presents)
        val phone = removeLeadingPlusIfFound(rawPhone);
        // ACTIVITY 2: create SHA 256 phone hash from phone number:
        String phoneHash = getPhoneHash(phone);
        // ACTIVITY 2: using a service find list of employees by phone hash:
        List<Employee> employees = getEmployeesByPhoneHash(phoneHash);
        val employeeCount = employees.size();

        return switch (employeeCount) {
            case 0 -> NOT_FOUND_EMPLOYEE_MESSAGE;
            case 1 -> authenticate(session, phoneHash, getEmployee(employees));
            default -> FOUND_MANY_EMPLOYEE_MESSAGE;
        };
    }

    private String authenticate(Session session, String phoneHash, Employee employee) {
        if (session == null) return null;

        updateSession(session, phoneHash);
        return AUTHENTICATED_MESSAGE.formatted(employee.firstNameEn(), employee.lastNameEn());
    }

    private void updateSession(Session session, String phoneHash) {
        val updatedSession = new Session(
                session.chatId(),
                LocalDateTime.now(),
                true,
                phoneHash
        );
        sessionService.persist(updatedSession);
    }

    private static Employee getEmployee(List<Employee> employees) {
        return employees.get(0);
    }

    private Session getSession(Long chatId) {
        return sessionService.getSession(chatId);
    }

    private List<Employee> getEmployeesByPhoneHash(String phoneHash) {
        return employeeService.findByPhone(phoneHash);
    }

    private static String getPhoneHash(String phone) {
        return DigestUtils.sha256Hex(phone);
    }

    private static String removeLeadingPlusIfFound(String phone) {
        return phone.charAt(0) == '+'
                ? phone.replaceFirst("^\\+", "")
                : phone;
    }
}
