package org.dental.backend.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dental.backend.domain.AppUser;
import org.dental.backend.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource("classpath:telegram.properties")
public class ChatBot extends TelegramLongPollingBot {

    @Autowired
    private AppUserService appUserService;

    private static final Logger LOGGER = LogManager.getLogger(ChatBot.class);

    private static final String BROADCAST = "broadcast ";
    private static final String LIST_USERS = "users";

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
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        final String text = update.getMessage().getText();
        final long chatId = update.getMessage().getChatId();

        final String firstName = update.getMessage().getChat().getFirstName();
        final String lastName = update.getMessage().getChat().getLastName();

        AppUser user = appUserService.findByChatId(chatId);

        BotContext context;
        BotState state;

        if (user == null) {
            state = BotState.getInitialState();

            user = appUserService.createUser(chatId, state, firstName, lastName);

            context = BotContext.of(this, user, text);
            state.enter(context);

            LOGGER.info("New user registered: " + chatId);
        }
        if (user.getStateId() != 0) {
            createVisit(user, text);
        }

        switch (text) {
            case "/start":
                sendMessage(chatId, "Привет, "+ firstName + " " + lastName+ "!\r\n" + GREETING_MESSAGE);
                break;
            case "Контакты":
                sendMessage(chatId, CONTACT_INFO);
                break;
            case "Запись":
                createVisit(user, text);
                break;
            default:
                sendMessage(chatId, DEFAULT_MESSAGE);
        }

        if (checkIfAdminCommand(chatId, text)) return;
    }

    private void createVisit(AppUser user, String text) {
        BotContext context;
        BotState state;

        context = BotContext.of(this, user, text);
        state = BotState.getStateById(user.getStateId());

        LOGGER.info("Update received for user in state: " + state);

        // handle input for personal user state
        state.handleInput(context);

        do {
            state = state.nextState(); // got to next state
            state.enter(context);      // enter to next state
        } while (!state.isInputNeeded());

        user.setStateId(state.ordinal());
        appUserService.updateUser(user);
    }

    private boolean checkIfAdminCommand(long chatId, String text) {
        AppUser user = appUserService.findByChatId(chatId);

        if (user == null || !user.getIsAdmin()) return false;

        if (text.startsWith(BROADCAST)) {
            LOGGER.info("Admin command received: " + BROADCAST);

            text = text.substring(BROADCAST.length());
            broadcast(text);

            return true;
        } else if (text.equals(LIST_USERS)) {
            LOGGER.info("Admin command received: " + LIST_USERS);

            getAllUsers(user);
            return true;
        }

        return false;
    }

    private void getAllUsers(AppUser admin) {
        StringBuilder sb = new StringBuilder("All user list: \r\n");

        List<AppUser> users = appUserService.findAllUsers();

        users.forEach(user ->
                sb.append(user.getFirstName())
                        .append(" ")
                        .append(user.getLastName())
                        .append(" | тел.")
                        .append(user.getPhone())
                        .append(" | ")
                        .append(user.getEmail())
                        .append(" | Статус: ")
                        .append(user.getStateId())
                        .append("\r\n")
        );

        sendMessage(admin.getChatId(), sb.toString());
    }

    private synchronized void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText(text);

        try {
            setButtons(message);
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String text) {
        List<AppUser> users = appUserService.findAllUsers();
        users.forEach(user -> sendMessage(user.getChatId(), text));
    }

    public synchronized void setButtons(SendMessage sendMessage) {
        // Create ReplyKeyboardMarkup object
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Create a keyboard row
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        keyboardFirstRow.add(new KeyboardButton("Запись"));
        keyboardFirstRow.add(new KeyboardButton("Контакты"));

        // Add the first row to the keyboard
        keyboard.add(keyboardFirstRow);
        // Set the keyboard to the markup
        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}
