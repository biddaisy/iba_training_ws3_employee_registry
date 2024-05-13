/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram;

import eu.ibagroup.bot.config.EmployeeRegistryBotConfig;
import eu.ibagroup.bot.telegram.command.CommandResult;
import eu.ibagroup.common.mongo.collection.Session;
import eu.ibagroup.common.service.SessionService;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.List;

/**
 * The bot main class, it periodically polls Telegram server and get updates from the server
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Slf4j
@Component
public class EmployeeRegistryBot extends TelegramLongPollingBot {

    private EmployeeRegistryBotConfig config;

    private EmployeeRegistryBotFacade registryBotFacade;

    private SessionService sessionService;

    public EmployeeRegistryBot(EmployeeRegistryBotConfig config, EmployeeRegistryBotFacade registryBotFacade, SessionService sessionService) {
        super(config.getAccessToken());
        this.config = config;
        this.registryBotFacade = registryBotFacade;
        this.sessionService = sessionService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = registryBotFacade.onUpdateReceived(update);
        sendMessage(update, msg);
    }

    public void sendMessage(Update update, CommandResult cr) {
        Long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId(); // when user typed and sends some text, or sends contact via reply button
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId(); // when user clicked an inline button (with employee name)
        } else {
            return; // otherwise, we cannot proceed
        }

        try {
            if (cr.hasPhoto()) {
                execute(buildPhotoMessage(chatId, cr));
            } else{
                execute(buildTextMessage(chatId,cr));
            }
        } catch (TelegramApiException telegramApiException) {
            log.error("Failed to send message '{}' to chatId={} because of error {}", cr.getText(), chatId, telegramApiException);
        }
    }

    private SendMessage buildTextMessage(Long chatId, CommandResult cr) {
        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder();
        messageBuilder.chatId(chatId.toString())
                .parseMode(ParseMode.HTML)
                .text(cr.getText());

        if (cr.getMarkup() != null) { // add keyboard if it was composed during the command execution
            messageBuilder.replyMarkup(cr.getMarkup());
        }

        // check if user not verified and add a button to verify phone
        Session session = sessionService.getSession(chatId);
        if (session != null && !session.isContactVerified()) { // we can get here only till user not verified
            // create keyboard and add to the message
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            // hide the button after verification, it should be used once only
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            replyKeyboardMarkup.setIsPersistent(false);

            // the keyboard contains 1 row  with 1 button
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText("Share your number to start using search function");
            keyboardButton.setRequestContact(true); // ask user to share phone number which used for telegram
            keyboardFirstRow.add(keyboardButton);

            // add the keyboard row
            keyboard.add(keyboardFirstRow);
            // set the keyboard to markup
            replyKeyboardMarkup.setKeyboard(keyboard);
            // set the markup to the reply message
            messageBuilder.replyMarkup(replyKeyboardMarkup);
        }

        return messageBuilder.build();
    }

    private SendPhoto buildPhotoMessage(Long chatId, CommandResult cr) {
        return cr.getPhoto();
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }
}
