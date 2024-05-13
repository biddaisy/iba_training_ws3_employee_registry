/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import com.vdurmont.emoji.EmojiParser;
import eu.ibagroup.bot.command.Command;
import eu.ibagroup.common.mongo.collection.Session;
import eu.ibagroup.common.service.SessionService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * This command prints status of your bot session, e.g. if contact is verified
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class StatusBotCommand extends BotCommand {

    private final SessionService sessionService;

    @Override
    public Command getCommand() {
        return Command.STATUS;
    }

    @Override
    public CommandResult execute(Update update) {
        val chatId = update.getMessage().getChatId();
        val session = sessionService.getSession(chatId);
        val text = EmojiParser.parseToUnicode("""
                :information_source: <b>Status of your session</b>
                :black_small_square: Verified: <b>%s</b>
                :black_small_square: Verified date: <b>%s</b>
                :man_technologist: Phone hash: <code>%s</code>
                """
                .formatted(
                        session.isContactVerified() ? "yes" : "no",
                        session.getVerifiedDate() != null ? session.getVerifiedDate() : "n/a",
                        session.getPhoneHash() != null ? session.getPhoneHash() : "n/a"
                )
        );
        return CommandResult.builder().text(text).build();
    }

    @Override
    public String getDescription() {
        return "Information about your session";
    }
}
