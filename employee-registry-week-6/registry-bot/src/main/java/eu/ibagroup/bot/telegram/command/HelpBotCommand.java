/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import eu.ibagroup.bot.command.Command;
import eu.ibagroup.bot.config.EmployeeRegistryBotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * This command print help message with list of all supported commands
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class HelpBotCommand extends BotCommand {

    private final EmployeeRegistryBotConfig config;

    @Override
    public Command getCommand() {
        return Command.HELP;
    }

    @Override
    public CommandResult execute(Update update) {
        return CommandResult.builder().text(config.getHelpMessage()).build();
    }

    @Override
    public String getDescription() {
        return "Helps the bot";
    }
}
