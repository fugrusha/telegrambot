package org.dental.backend.bot;

import lombok.Getter;
import lombok.Setter;
import org.dental.backend.domain.AppUser;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
@Setter
public class BotContext {

    private final ChatBot bot;
    private final AppUser user;
    private final Message message;

    public static BotContext of(ChatBot bot, AppUser user, Message message) {
        return new BotContext(bot, user, message);
    }

    public BotContext(ChatBot bot, AppUser user, Message message) {
        this.bot = bot;
        this.user = user;
        this.message = message;
    }
}
