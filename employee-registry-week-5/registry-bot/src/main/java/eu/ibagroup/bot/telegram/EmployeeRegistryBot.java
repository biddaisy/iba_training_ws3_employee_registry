/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram;

import eu.ibagroup.bot.config.EmployeeRegistryBotConfig;
import eu.ibagroup.bot.telegram.command.CommandResult;
import eu.ibagroup.common.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

/**
 * The bot main class, it periodically polls Telegram server and get updates from the server
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Slf4j
@Component
public class EmployeeRegistryBot extends TelegramLongPollingBot {

    private final EmployeeRegistryBotConfig config;
    private final EmployeeRegistryBotFacade registryBotFacade;
    private final SessionService sessionService;

    public EmployeeRegistryBot(
            EmployeeRegistryBotConfig config,
            EmployeeRegistryBotFacade registryBotFacade,
            SessionService sessionService
    ) {
        super(config.getAccessToken());
        this.config = config;
        this.registryBotFacade = registryBotFacade;
        this.sessionService = sessionService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        val commandResult = registryBotFacade.onUpdateReceived(update);
        sendMessage(update, commandResult);
    }

    public void sendMessage(Update update, CommandResult commandResult) {
        val chatId = getChatId(update);
        if (chatId == null) {
            // otherwise, we cannot proceed
            return;
        }
        execute(commandResult, chatId);
    }

    private void execute(CommandResult commandResult, Long chatId) {
        try {
            if (commandResult.hasPhoto()) {
                execute(buildPhotoMessage(commandResult));
            } else {
                execute(buildTextMessage(chatId, commandResult));
            }
        } catch (TelegramApiException telegramApiException) {
            logError(commandResult, telegramApiException, chatId);
        }
    }

    private static void logError(CommandResult commandResult, TelegramApiException telegramApiException, Long chatId) {
        log.error("Failed to send message '{}' to chatId={} because of error {}",
                commandResult.text(),
                chatId,
                telegramApiException.getMessage(),
                telegramApiException
        );
    }

    private static Long getChatId(Update update) {
        if (update.hasMessage()) {
            // when user typed and sends some text, or sends contact via reply button
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            // when user clicked an inline button (with employee name)
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return null;
    }

    private SendMessage buildTextMessage(Long chatId, CommandResult commandResult) {
        val messageBuilder = SendMessage.builder();
        setText(chatId, commandResult, messageBuilder);
        setMarkup(commandResult, messageBuilder);
        setKeyboardIfContactNotVerified(chatId, messageBuilder);
        return messageBuilder.build();
    }

    private void setKeyboardIfContactNotVerified(Long chatId, SendMessage.SendMessageBuilder messageBuilder) {
        // check if user not verified and add a button to verify phone
        if (!isUserVerified(chatId)) { // we can get here only till user not verified
            // create keyboard and add to the message
            val replyKeyboardMarkup = getReplyKeyboardMarkup();
            // set the markup to the reply message
            messageBuilder.replyMarkup(replyKeyboardMarkup);
        }
    }

    private boolean isUserVerified(long chatId) {
        return sessionService.isUserVerified(chatId);
    }

    private static ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        // the keyboard contains 1 row  with 1 button
        val keyboard = List.of(getKeyboardFirstRow());
        return ReplyKeyboardMarkup.builder()
                // hide the button after verification, it should be used once only
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .isPersistent(false)
                // set the keyboard to markup
                .keyboard(keyboard)
                .build();
    }

    private static KeyboardRow getKeyboardFirstRow() {
        return new KeyboardRow(List.of(getKeyboardButton()));
    }

    private static KeyboardButton getKeyboardButton() {
        return KeyboardButton.builder()
                .text("Share your number to start using search function")
                .requestContact(true)
                .build();// ask user to share phone number which used for telegram
    }

    private static void setMarkup(CommandResult commandResult, final SendMessage.SendMessageBuilder messageBuilder) {
        val markup = commandResult.markup();
        if (markup != null) { // add keyboard if it was composed during the command execution
            messageBuilder.replyMarkup(markup);
        }
    }

    private static void setText(Long chatId, CommandResult commandResult, final SendMessage.SendMessageBuilder messageBuilder) {
        messageBuilder
                .chatId(chatId.toString())
                .parseMode(ParseMode.HTML)
                .text(commandResult.text());
    }

    private SendPhoto buildPhotoMessage(CommandResult commandResult) {
        return commandResult.photo();
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }
}
