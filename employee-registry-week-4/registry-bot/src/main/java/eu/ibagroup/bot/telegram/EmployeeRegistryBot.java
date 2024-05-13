/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram;

import eu.ibagroup.bot.config.EmployeeRegistryBotConfig;
import eu.ibagroup.common.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

/**
 * The bot main class, it periodically polls Telegram server and get updates from the servers
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
        sendMessage(update, registryBotFacade.onUpdateReceived(update));
    }

    public void sendMessage(Update update, String messageText) {
        if (!update.hasMessage()) return;

        val chatId = getChatId(update);
        val messageBuilder = getSendMessageBuilder(messageText, chatId);

        if (isUserNotVerified(chatId)) {
            messageBuilder.replyMarkup(getReplyKeyboardMarkup());
        }

        sendMessage(messageText, messageBuilder, chatId);
    }

    public static Long getChatId(Update update) {
        return update.getMessage().getChatId();
    }

    // get the user's input, note it can be both command or an arbitrary value
    public static String getInputMessage(Update update) {
        return update.getMessage().getText();
    }

    private void sendMessage(String messageText, SendMessage.SendMessageBuilder messageBuilder, Long chatId) {
        try {
            execute(messageBuilder.build());
        } catch (TelegramApiException telegramApiException) {
            logError(messageText, telegramApiException, chatId);
        }
    }

    private static SendMessage.SendMessageBuilder getSendMessageBuilder(String messageText, Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .parseMode(ParseMode.HTML)
                .text(messageText);
    }

    private boolean isUserNotVerified(Long chatId) {
        return !sessionService.isUserVerified(chatId);
    }

    private static void logError(String messageText, TelegramApiException telegramApiException, Long chatId) {
        log.error(
                "Failed to send message '{}' to chatId={} because of error {}",
                messageText,
                chatId,
                telegramApiException.getMessage(),
                telegramApiException);
    }

    private static ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        // create keyboard and add to the message
        return ReplyKeyboardMarkup.builder()
                // customize the keyboard markup
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .isPersistent(false)
                // add keyboard rows to our keyboard
                .keyboard(getKeyboardRows())
                .build();
    }

    private static List<KeyboardRow> getKeyboardRows() {
        // add array to list
        return List.of(new KeyboardRow(getKeyboardButton()));
    }

    private static List<KeyboardButton> getKeyboardButton() {
        return List.of(
                KeyboardButton.builder()
                        .text("Share your number to start using search function")
                        .requestContact(true)
                        .build()
        );
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }
}
