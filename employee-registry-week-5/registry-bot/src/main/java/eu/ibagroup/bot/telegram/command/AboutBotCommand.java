/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import com.vdurmont.emoji.EmojiParser;
import eu.ibagroup.bot.command.Command;
import eu.ibagroup.bot.config.Version;
import eu.ibagroup.common.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.InetAddress;
import java.time.LocalDate;

/**
 * The command returns information abot the bot (version, build date, etc.)
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class AboutBotCommand extends BotCommand {

    private final SessionService sessionService;

    @Override
    public Command getCommand() {
        return Command.ABOUT;
    }

    @SneakyThrows
    @Override
    public CommandResult execute(Update update) {

        val year = LocalDate.now().getYear();
        val res = EmojiParser.parseToUnicode("""
                :hammer_and_pick: <b>Build Information</b>
                :arrow_right: Bot version: %s
                :arrow_right: Build time: %s
                :arrow_right: Build revision: %s%n
                :information_source: <b>Information</b>
                :black_small_square: Number of user sessions: <b>%d</b>
                :black_small_square: Host: <code>%s</code>
                :man_technologist: Author: @mzaikin © %d                                
                """.formatted(Version.POM_VERSION,Version.BUILD_TIME, Version.BUILD_NUMBER, sessionService.count(), InetAddress.getLocalHost(), year));

        return CommandResult.builder().text(res).build();
    }

    @Override
    public String getDescription() {
        return "Information about the bot";
    }
}
