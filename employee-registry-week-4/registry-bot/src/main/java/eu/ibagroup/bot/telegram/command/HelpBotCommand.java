/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import eu.ibagroup.bot.config.MessageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * This command print help message with list of all supported commands
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class HelpBotCommand extends BotCommand {

    private final MessageHelper messageHelper;

    @Override
    public Command getCommand() {
        return Command.HELP;
    }

    @Override
    public String execute(Update update) {
        return messageHelper.getHelpMessage();
    }

    @Override
    public String getDescription() {
        return "Helps the bot";
    }

}
