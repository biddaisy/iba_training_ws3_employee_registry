/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import com.vdurmont.emoji.EmojiParser;
import eu.ibagroup.common.mongo.collection.Session;
import eu.ibagroup.common.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static eu.ibagroup.bot.telegram.EmployeeRegistryBot.getChatId;

/**
 * This command prints status of your bot session, e.g. if contact is verified
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class StatusBotCommand extends BotCommand {

    private static final String N_A = "n/a";

    private final SessionService sessionService;

    @Override
    public Command getCommand() {
        return Command.STATUS;
    }

    @Override
    public String execute(Update update) {
        Session session = getSession(getChatId(update));
        return EmojiParser.parseToUnicode("""
                :information_source: <b>Status of your session</b>
                :black_small_square: Verified: <b>%s</b>
                :black_small_square: Verified date: <b>%s</b>
                :man_technologist: Phone hash: <code>%s</code>
                """.formatted(verifiedMessage(session), verifiedDateMessage(session), phoneHashMessage(session)));
    }

    private Session getSession(Long chatId) {
        return sessionService.getSession(chatId);
    }

    private static String phoneHashMessage(Session session) {
        return session.phoneHash() != null ? session.phoneHash() : N_A;
    }

    private static Object verifiedDateMessage(Session session) {
        return session.verifiedDate() != null ? session.verifiedDate() : N_A;
    }

    private static String verifiedMessage(Session session) {
        return session.isContactVerified() ? "yes" : "no";
    }

    @Override
    public String getDescription() {
        return "Information about your session";
    }
}
