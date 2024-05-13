/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.config;

import eu.ibagroup.bot.command.Command;
import eu.ibagroup.bot.command.CommandHelper;
import eu.ibagroup.bot.telegram.command.BotCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

/**
 * Factory class for BotCommand.
 * Creates BotCommand based on user's input.
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class EmployeeRegistryBotCommandFactory {

    private final CommandHelper botConfiguration;

    public BotCommand getBotCommand(Command command) {
        return botConfiguration.getBotCommand(command);
    }

    // decide which command the bot needs to execute
    public BotCommand getBotCommand(Update update) {
        return Optional.of(update)
                .map(this::getBotCommandByUpdate)
                .orElseThrow();
    }

    private BotCommand getBotCommandByUpdate(Update update) {
        return getBotCommandByMessage(update.getMessage())
                .orElseGet(() -> getBotCommandByCallbackQuery(update.getCallbackQuery()));
    }

    private Optional<BotCommand> getBotCommandByMessage(Message message) {
        return Optional
                .ofNullable(message)
                .map(m -> getBotCommandByText(m.getText()));
    }

    private BotCommand getBotCommandByText(String messageText) {
        return Optional
                .ofNullable(botConfiguration.getBotCommand(messageText))
                .orElseGet(() -> botConfiguration.getBotCommand(Command.FIND_EMPLOYEES));
    }

    private BotCommand getBotCommandByCallbackQuery(CallbackQuery callbackQuery) {
        return Optional.ofNullable(callbackQuery)
                .map(this::getBotCommandByQueryData)
                .orElseGet(() -> botConfiguration.getBotCommand(Command.HELP));
    }

    private BotCommand getBotCommandByQueryData(CallbackQuery cq) {
        return Optional.ofNullable(cq.getData())
                .map(q -> botConfiguration.getBotCommand(Command.GET_EMPLOYEE))
                .orElseGet(() -> botConfiguration.getBotCommand(Command.HELP));
    }
}
