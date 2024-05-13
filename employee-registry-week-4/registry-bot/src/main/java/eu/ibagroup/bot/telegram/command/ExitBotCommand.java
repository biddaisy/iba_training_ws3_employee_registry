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
 * This command cleans up all user information
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class ExitBotCommand extends BotCommand {

    private final SessionService sessionService;

    @Override
    public Command getCommand() {
        return Command.EXIT;
    }

    @Override
    public String execute(Update update) {
        sessionService.deleteSession(getChatId(update));
        return "All your data has been deleted. Good bye!";
    }

    @Override
    public String getDescription() {
        return "Clean up all your data from the application";
    }
}
