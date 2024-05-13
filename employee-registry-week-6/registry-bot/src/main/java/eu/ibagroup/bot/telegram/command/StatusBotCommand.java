/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import com.vdurmont.emoji.EmojiParser;
import eu.ibagroup.bot.command.Command;
import eu.ibagroup.common.mongo.collection.Session;
import eu.ibagroup.common.service.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * This command prints status of your bot session, e.g. if contact is verified
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Component
@AllArgsConstructor
public class StatusBotCommand extends BotCommand {

    private final SessionService sessionService;

    @Override
    public Command getCommand() {
        return Command.STATUS;
    }

    @Override
    public CommandResult execute(Update update) {
        var chatId = update.getMessage().getChatId();
        Session s = sessionService.getSession(chatId);
        var res = EmojiParser.parseToUnicode("""
                :information_source: <b>Status of your session</b>
                :black_small_square: Verified: <b>%s</b>
                :black_small_square: Verified date: <b>%s</b>
                :man_technologist: Phone hash: <code>%s</code>
                """.formatted(s.isContactVerified() ? "yes" : "no", s.getVerifiedDate() != null ? s.getVerifiedDate() : "n/a",  s.getPhoneHash() != null ? s.getPhoneHash() : "n/a"));
        return CommandResult.builder().text(res).build();
    }

    @Override
    public String getDescription() {
        return "Information about your session";
    }
}
