/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.command;

import eu.ibagroup.bot.config.EmployeeRegistryBotConfig;
import eu.ibagroup.bot.telegram.command.BotCommand;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class for working with commands
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Component
@RequiredArgsConstructor
public class CommandHelper {

    private final EmployeeRegistryBotConfig config;

    private final ListableBeanFactory beanFactory;

    /**
     * List of supported commands for this bot
     */
    private List<Command> commands;

    private Map<Command, BotCommand> botCommands;

    private Map<String, BotCommand> botCommandsByString;

    private String helpMessage;

    @PostConstruct
    public void init() {
        botCommands = buildBotCommands();
        botCommandsByString = buildBotCommandsByString();
        val botCommandsMsg = buildHelpMessage();
        config.setHelpMessage(botCommandsMsg);
    }

    private String buildHelpMessage() {
        return Stream.of(Command.values())
                .map(c -> botCommands.get(c))
                .map(CommandHelper::getCommandDescription)
                .collect(Collectors.joining("\n"));
    }

    private static String getCommandDescription(BotCommand bc) {
        return bc.toString() + " -- " + bc.getDescription() + " " + (bc.isAnonymous() ? "" : "(<i>requires authentication</i>)");
    }

    private Map<String, BotCommand> buildBotCommandsByString() {
        return beanFactory.getBeansOfType(BotCommand.class).values().stream()
                .collect(Collectors.toMap(BotCommand::toString, Function.identity()));
    }

    private Map<Command, BotCommand> buildBotCommands() {
        return beanFactory.getBeansOfType(BotCommand.class).values().stream()
                .collect(Collectors.toMap(BotCommand::getCommand, Function.identity()));
    }

    public BotCommand getBotCommand(Command command) {
        return botCommands.get(command);
    }

    public BotCommand getBotCommand(String cmd) {
        return botCommandsByString.get(cmd);
    }
}
