/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import eu.ibagroup.common.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static eu.ibagroup.bot.telegram.EmployeeRegistryBot.getChatId;

/**
 * This command searches Employees by last name
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class FindEmployeesBotCommand extends BotCommand {

    private static final String USER_NOT_VERIFIED_MESSAGE = "You need to pass verification to use this command";
    private static final String NOT_YET_IMPLEMENTED = "NOT YET IMPLEMENTED";

    private final SessionService sessionService;

    @Override
    public Command getCommand() {
        return Command.FIND_EMPLOYEES;
    }

    @Override
    public String execute(Update update) {
        return isUserVerified(getChatId(update))
                ? NOT_YET_IMPLEMENTED
                : USER_NOT_VERIFIED_MESSAGE;
    }

    private boolean isUserVerified(Long chatId) {
        return sessionService.isUserVerified(chatId);
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
