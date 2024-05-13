/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * This command prints welcome message for new users
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Component
public class StartBotCommand extends BotCommand {

    @Override
    public Command getCommand() {
        return Command.START;
    }

    @Override
    public String execute(Update update) {
        return "The bot was started! You may want to read how to use the bot first, use /" + Command.HELP.name().toLowerCase();
    }

    @Override
    public String getDescription() {
        return "Starts the bot";
    }

}
