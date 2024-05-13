/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.config;

import eu.ibagroup.bot.command.Command;
import eu.ibagroup.bot.command.CommandHelper;
import eu.ibagroup.bot.telegram.command.BotCommand;
import eu.ibagroup.common.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Factory class for BotCommand.
 * Creates BotCommand based on user's input.
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeRegistryBotCommandFactory {

    private final CommandHelper botConfiguration;

    private final SessionService sessionService;

    // decide which command the bot needs to execute
    public BotCommand getBotCommand(Update update) {
        BotCommand commandToRun = null;

        Message message = update.getMessage();

        if (message != null) { // some text was sent by user to the bot
            // retrieve chat ID
            var chatId = message.getChatId();

            // check if conversation has started, if not -- start conversation
            if (sessionService.isChatNew(chatId)) {
                sessionService.initChat(chatId);
                return botConfiguration.getBotCommand(Command.START);
            }

            // get the user's input, note it can be both command or a value (e.g. email)
            String inputMessageText = update.getMessage().getText();

            commandToRun = botConfiguration.getBotCommand(inputMessageText);

            if (commandToRun == null) {
                commandToRun = botConfiguration.getBotCommand(Command.FIND_EMPLOYEES);
            }
        } else { //  the InlineKeyboardButton was clicked - it has no message, but has call back instead
            CallbackQuery cbq = update.getCallbackQuery();
            if ((cbq != null) && (cbq.getData() != null)) {
                commandToRun = botConfiguration.getBotCommand(Command.GET_EMPLOYEE);
            }
        }

        return commandToRun == null ? botConfiguration.getBotCommand(Command.HELP) : commandToRun;
    }
}
