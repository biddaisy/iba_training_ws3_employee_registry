/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.bot.telegram.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * This class encapsulates bot command execution result.
 * It may contain a text, a keyboard, or a photo, or a combination of those.
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Builder
public record CommandResult(
        String text,
        InlineKeyboardMarkup markup,
        SendPhoto photo
) {
    public boolean hasPhoto() {
        return photo != null;
    }
}
