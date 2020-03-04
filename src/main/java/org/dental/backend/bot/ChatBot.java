package org.dental.backend.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dental.backend.dto.AppUserCreateDTO;
import org.dental.backend.dto.AppUserReadDTO;
import org.dental.backend.service.AdminService;
import org.dental.backend.service.AppUserService;
import org.dental.backend.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@PropertySource("classpath:telegram.properties")
public class ChatBot extends TelegramLongPollingBot {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private BotCommand botCommand;

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
        final String username = update.getMessage().getChat().getUserName();

        AppUserReadDTO userDTO = appUserService.findByChatId(chatId);

        if (userDTO == null) {
            AppUserCreateDTO createDTO = new AppUserCreateDTO();
            createDTO.setChatId(chatId);
            createDTO.setFirstName(firstName);
            createDTO.setLastName(lastName);
            createDTO.setUsername(username);

            userDTO = appUserService.createUser(createDTO);

            LOGGER.info("New user registered: " + chatId);
        }
        if (userDTO.getStateId() != 0) {
            visitService.createVisit(this, userDTO, text);
        }

        if (checkIfAdminCommand(chatId, text)) return;

        switch (text) {
            case "/start":
                sendMessage(chatId, "Привет, "+ firstName + " " + lastName+ "!\r\n" + GREETING_MESSAGE);
                break;
            case "Контакты":
                sendMessage(chatId, CONTACT_INFO);
                break;
            case "Запись":
                visitService.createVisit(this, userDTO, text);
                break;
            default:
                sendMessage(chatId, DEFAULT_MESSAGE);
        }


    }

    public synchronized void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText(text);

        try {
            botCommand.setButtons(message);
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfAdminCommand(long chatId, String text) {
        AppUserReadDTO user = appUserService.findByChatId(chatId);

        if (user == null || !user.getIsAdmin()) return false;

        if (text.startsWith(BROADCAST)) {
            LOGGER.info("Admin command received: " + BROADCAST);

            text = text.substring(BROADCAST.length());
            adminService.broadcast(text);

            return true;
        } else if (text.equals(LIST_USERS)) {
            LOGGER.info("Admin command received: " + LIST_USERS);

            adminService.getAllUsers(user);
            return true;
        }

        return false;
    }


}
