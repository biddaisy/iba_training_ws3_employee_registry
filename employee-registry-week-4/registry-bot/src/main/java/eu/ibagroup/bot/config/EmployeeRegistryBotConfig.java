/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.config;

import eu.ibagroup.bot.telegram.command.Command;
import eu.ibagroup.bot.telegram.command.BotCommand;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class provides access to properties file
 * Also contains utility methods to get commands
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@ConfigurationProperties("bot")
@Component
@RequiredArgsConstructor
public class EmployeeRegistryBotConfig {

    private final ListableBeanFactory beanFactory;

    private final MessageHelper messageHelper;

    /**
     * Contains TG bot name picked during bot registration
     */
    @Getter
    @Setter
    private String name;

    /**
     * Contains TG bot access token assigned during bot registration
     */
    @Getter
    @Setter
    private String accessToken;

    /**
     * List of supported commands for this bot
     */
    @Getter
    private List<Command> commands;

    private Map<Command, BotCommand> botCommands;
    private Map<String, BotCommand> botCommandsByString;

    @PostConstruct
    public void init() {
        botCommands = getBotCommands();
        botCommandsByString = getBotCommandsByString();
        commands = List.of(Command.values());
        messageHelper.setHelpMessage(getHelpMessage());
    }


    public BotCommand getBotCommand(Command command) {
        return botCommands.get(command);
    }

    public Optional<BotCommand> getBotCommand(String commandName) {
        return Optional.ofNullable(botCommandsByString.get(commandName));
    }

    private String getHelpMessage() {
        return commands.stream()
                .map(c -> botCommands.get(c))
                .map(EmployeeRegistryBotConfig::getBotCommandHelpMessage)
                .collect(Collectors.joining("\n"));
    }

    private static String getBotCommandHelpMessage(BotCommand botCommand) {
        return botCommand.toString()
                + " -- "
                + botCommand.getDescription()
                + " "
                + (botCommand.isAnonymous() ? "" : "(<i>requires authentication</i>)");
    }

    private Map<String, BotCommand> getBotCommandsByString() {
        return beanFactory.getBeansOfType(BotCommand.class).values().stream()
                .collect(Collectors.toMap(BotCommand::toString, Function.identity()));
    }

    private Map<Command, BotCommand> getBotCommands() {
        return beanFactory.getBeansOfType(BotCommand.class).values().stream()
                .collect(Collectors.toMap(BotCommand::getCommand, Function.identity()));
    }

}
