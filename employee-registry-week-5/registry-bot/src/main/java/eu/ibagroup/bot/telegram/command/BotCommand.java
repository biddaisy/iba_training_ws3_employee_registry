/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import eu.ibagroup.bot.command.Command;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Parent class for all commands.
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Slf4j
public abstract class BotCommand {
    public abstract Command getCommand();

    public boolean isAnonymous() {
        return getCommand().isAnonymous();
    }

    public abstract CommandResult execute(Update update);

    public abstract String getDescription();

    public String toString() {
        return "/" + getCommand().name().toLowerCase();
    }

    static CommandResult getUserNotVerifiedCommandResult() {
        val message = "You need to pass verification to use this command";
        return CommandResult.builder().text(message).build();
    }
}
