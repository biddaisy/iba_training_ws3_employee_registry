/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.config;

import eu.ibagroup.bot.telegram.command.BotCommand;
import eu.ibagroup.bot.telegram.command.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory class for BotCommand.
 * Creates BotCommand based on user's input.
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeRegistryBotCommandFactory {

    private final EmployeeRegistryBotConfig botConfiguration;

    // decide which command the bot needs to execute
    public BotCommand getBotCommand(String inputMessage) {

        return botConfiguration.getBotCommand(inputMessage)
                // if command has no fixed pattern, then it is an employee search pattern
                .orElseGet(() -> getBotCommand(Command.FIND_EMPLOYEES));
    }

    public BotCommand getBotCommand(Command command) {
        return botConfiguration.getBotCommand(command);
    }

}
