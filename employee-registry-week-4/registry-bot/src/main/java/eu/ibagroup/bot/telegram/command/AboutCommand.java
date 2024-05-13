package eu.ibagroup.bot.telegram.command;

import com.vdurmont.emoji.EmojiParser;
import eu.ibagroup.bot.config.Version;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class AboutCommand extends BotCommand {

    @Override
    public Command getCommand() {
        return Command.ABOUT;
    }

    @Override
    public String execute(Update update) {
        return EmojiParser.parseToUnicode("""
                :information_source: <b>Build info</b>
                :black_small_square: Build number: <b>%s</b>
                :black_small_square: Build time: <b>%s</b>
                :man_technologist: Version: <code>%s</code>
                """.formatted(Version.BUILD_NUMBER, Version.BUILD_TIME, Version.POM_VERSION));
    }

    @Override
    public String getDescription() {
        return "About the bot";
    }
}
