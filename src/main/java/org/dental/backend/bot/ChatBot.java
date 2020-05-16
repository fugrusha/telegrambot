package org.dental.backend.bot;

import lombok.extern.slf4j.Slf4j;
import org.dental.backend.domain.AppUser;
import org.dental.backend.service.AdminService;
import org.dental.backend.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
public class ChatBot extends TelegramLongPollingBot {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private BotCommand botCommand;

    private final static String GREETING_MESSAGE = "Меня зовут Молочный Зуб, я буду твоим помощником.\r\n"
            + "Для общения со мной используйте какую-либо из команд ниже.";

    private final static String CONTACT_INFO = "*Доктор:* Сергей Босый\r\n"
            + "*Телефон:* 066-123-45-67\r\n"
            + "*Адрес:* ул. Шишкина, дом Коротышкина";

    private final static String DEFAULT_MESSAGE = "Для общения со мной используйте какую-либо из команд ниже.";

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("New message received");

        final Message message = update.getMessage();
        final long chatId = update.getMessage().getChatId();

        final String firstName = update.getMessage().getChat().getFirstName();
        final String lastName = update.getMessage().getChat().getLastName();
        final String username = update.getMessage().getChat().getUserName();

        AppUser user = appUserService.findByChatId(chatId);

        if (user == null) {
            user = appUserService.createUser(chatId, firstName, lastName, username);

            log.info("New user was registered: " + chatId);
        }

        // when user sends contact number
        if (message.hasContact()) {
            updateBotContext(this, user, message);
            return;
        }

        if (adminService.checkIfAdminCommand(chatId, message.getText())) return;

        switch (message.getText()) {
            case "/start":
                sendMessage(chatId, "Привет, " + firstName + "!\r\n" + GREETING_MESSAGE, null);
                break;
            case BotCommand.CONTACT_BUTTON:
                sendMessage(chatId, CONTACT_INFO, null);
                break;
            case BotCommand.VISIT_BUTTON:
                updateBotContext(this, user, message);
                break;
            case "В главное меню":
                user.setStateId(0);
                appUserService.updateUser(user);
                sendMessage(chatId, DEFAULT_MESSAGE, null);
                break;
            default:
                if (user.getStateId() != 0) {
                    updateBotContext(this, user, message);
                    return;
                }

                sendMessage(chatId, DEFAULT_MESSAGE, null);
        }
    }

    public void updateBotContext(ChatBot bot, AppUser user, Message message) {
        BotContext context = BotContext.of(bot, user, message);
        BotState state = BotState.getStateById(user.getStateId());

        log.info("Update received for user in state: " + state);

        // handle input for personal user state
        state.handleInput(context);

        do {
            state = state.nextState(); // got to next state
            state.enter(context);      // enter to next state
        } while (!state.isInputNeeded());

        user.setStateId(state.ordinal());
        appUserService.updateUser(user);
    }

    public void sendMessage(Long chatId, String text, List<String> buttonNames) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText(text);

        try {
            botCommand.setButtons(message, buttonNames);
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
