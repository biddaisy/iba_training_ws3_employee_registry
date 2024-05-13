/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Enum lists commands supported by the Bot
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Command {

    START,
    HELP,
    ABOUT,
    FIND_EMPLOYEES(false),
    STATUS,
    EXIT;

    private boolean anonymous = true;

}

