/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram;

import eu.ibagroup.bot.config.EmployeeRegistryBotCommandFactory;
import eu.ibagroup.bot.telegram.command.CommandResult;
import eu.ibagroup.common.mongo.collection.Employee;
import eu.ibagroup.common.mongo.collection.Session;
import eu.ibagroup.common.service.EmployeeService;
import eu.ibagroup.common.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The bot facade class
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class EmployeeRegistryBotFacade {

    private final EmployeeRegistryBotCommandFactory commandFactory;

    private final SessionService sessionService;

    private final EmployeeService employeeService;

    protected CommandResult onUpdateReceived(Update update) {

        // try to authenticate user if s/he passed contact information, it may happen on any stage
        if (update.hasMessage() && update.getMessage().getContact() != null) {
            Contact contact = update.getMessage().getContact();
            String phone = update.getMessage().getContact().getPhoneNumber();
            if ((phone != null) && !phone.isEmpty()) {
                phone = phone.replaceAll("[^0-9]", ""); // cut the '+' sign prepended by Telegram
                String phoneHash = DigestUtils.sha256Hex(phone);
                List<Employee> empList = employeeService.findByPhone(phoneHash);
                var res = switch (empList.size()) {
                    case 0: {
                        yield "There is no employee who provided this number in DB. Please try to authenticate from a different number";
                    }
                    case 1: {
                        Session session = sessionService.getSession(update.getMessage().getChatId());
                        if (session != null) {
                            session.setContactVerified(true);
                            session.setVerifiedDate(LocalDateTime.now());
                            session.setPhoneHash(phoneHash);
                            sessionService.persist(session);
                            Employee emp = empList.get(0);
                            yield "Welcome %s %s !%nYou are now eligible to search employees and browse employee info.".formatted(emp.getFirstNameEn(), emp.getLastNameEn());
                        }
                    }
                    default: {
                        yield "There are several employees who provided this number in DB, please try to authenticate from a different unique number";
                    }
                };
                return CommandResult.builder().text(res).build();
            }
        }

        var bc = commandFactory.getBotCommand(update);
        return bc.execute(update);
    }
}
