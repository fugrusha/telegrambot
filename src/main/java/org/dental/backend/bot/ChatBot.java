package org.dental.backend.bot;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.dental.backend.domain.AppUser;
import org.dental.backend.domain.Constants;
import org.dental.backend.service.AdminService;
import org.dental.backend.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static org.dental.backend.bot.KeyboardFactory.CALL_BUTTON;

@Slf4j
@Component
public class ChatBot extends TelegramLongPollingBot {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private KeyboardFactory keyboardFactory;

    private final static String GREETING_MESSAGE = EmojiParser.parseToUnicode(":hand: \n\nМеня зовут Молочный Зуб, я буду твоим помощником.\r\n"
            + "Для общения со мной используйте какую-либо из команд ниже :arrow_down:");

    private final static String DEFAULT_MESSAGE = EmojiParser.parseToUnicode("Для общения со мной используйте какую-либо из команд ниже :arrow_down:");

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

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().equals(CALL_BUTTON)) {
            sendMessage(user.getChatId(), "Андрей 0978592859", null);
            return;
        }

        if (adminService.checkIfAdminCommand(chatId, message.getText())) return;

        switch (message.getText()) {
            case "/start":
                sendMessage(chatId, "Привет, " + firstName + "!\r\n" + GREETING_MESSAGE, null);
                break;
            case KeyboardFactory.CONTACT_BUTTON:
                replyToLocationButton(chatId);
                break;
            case KeyboardFactory.QUESTION_BUTTON:
                replyToContactButton(chatId);
                break;
            case KeyboardFactory.VISIT_BUTTON:
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
        message.setReplyMarkup(KeyboardFactory.getButtons(buttonNames));

        send(message);
    }

    private void replyToLocationButton(long chatId) {
        SendVenue venue = new SendVenue()
                .setChatId(chatId)
                .setTitle(Constants.VENUE_TITLE)
                .setAddress(Constants.ADDRESS)
                .setLatitude(Constants.LATITUDE)
                .setLongitude(Constants.LONGITUDE);

        send(venue);
    }

    // FIXME
    private void sendContact(long chatId) {
        SendContact contact = new SendContact()
                .setChatId(chatId)
                .setFirstName("Андрей")
                .setLastName("Головко")
                .setPhoneNumber("+380978592859");

        send(contact);
    }

    private void replyToContactButton(long chatId) {
        SendMessage message = new SendMessage()
                .setText(DEFAULT_MESSAGE)
                .setChatId(chatId)
                .setReplyMarkup(KeyboardFactory.getContactsButtons());

        send(message);
    }

    private void send(BotApiMethod<Message> message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
