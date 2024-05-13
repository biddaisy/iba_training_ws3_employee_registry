/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import eu.ibagroup.bot.command.Command;
import eu.ibagroup.common.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * This command cleans up all user information
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
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
    public CommandResult execute(Update update) {
        val chatId = update.getMessage().getChatId();
        sessionService.deleteSession(chatId);
        return CommandResult.builder().text("All your data has been deleted. Good bye!").build();
    }

    @Override
    public String getDescription() {
        return "Clean up all your data from the application";
    }
}
