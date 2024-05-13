/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.command;

/**
 * Enum lists commands supported by the Bot
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
public enum Command {
    START,
    HELP,
    FIND_EMPLOYEES(false),
    GET_EMPLOYEE(false),
    ABOUT,
    STATUS,
    EXIT;

    private boolean isAnonymous = true;

    Command() {}

    Command(boolean ia) {
        isAnonymous = ia;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }
}

