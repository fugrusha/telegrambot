package org.dental.backend.bot;

import lombok.Getter;
import lombok.Setter;
import org.dental.backend.dto.AppUserReadDTO;

@Getter
@Setter
public class BotContext {

    private final ChatBot bot;
    private final AppUserReadDTO user;
    private final String input;

    public static BotContext of(ChatBot bot, AppUserReadDTO user, String text) {
        return new BotContext(bot, user, text);
    }

    public BotContext(ChatBot bot, AppUserReadDTO user, String input) {
        this.bot = bot;
        this.user = user;
        this.input = input;
    }
}
