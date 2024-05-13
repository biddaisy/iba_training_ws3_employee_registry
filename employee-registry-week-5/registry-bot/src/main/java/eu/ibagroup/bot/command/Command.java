/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum lists commands supported by the Bot
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Getter
@RequiredArgsConstructor
public enum Command {
    START,
    HELP,
    FIND_EMPLOYEES(false),
    GET_EMPLOYEE(false),
    ABOUT,
    STATUS,
    EXIT;

    private final boolean anonymous;

    Command() {
        anonymous = true;
    }

}

