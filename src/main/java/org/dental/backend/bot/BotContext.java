package org.dental.backend.bot;

import lombok.Getter;
import lombok.Setter;
import org.dental.backend.domain.AppUser;

@Getter
@Setter
public class BotContext {

    private final ChatBot bot;
    private final AppUser user;
    private final String input;

    public static BotContext of(ChatBot bot, AppUser user, String text) {
        return new BotContext(bot, user, text);
    }

    public BotContext(ChatBot bot, AppUser user, String input) {
        this.bot = bot;
        this.user = user;
        this.input = input;
    }
}
