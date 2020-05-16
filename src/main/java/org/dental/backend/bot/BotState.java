package org.dental.backend.bot;

import lombok.extern.slf4j.Slf4j;
import org.dental.backend.utils.Utils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public enum BotState {

    START(false) {
        @Override
        public void enter(BotContext context) {
            sendMessage(context,
                    "Для записи Вам необходимо выполнить (завершить) регистрацию.",
                    null);
        }

        @Override
        public BotState nextState() {
            return ENTER_PHONE;
        }
    },

    ENTER_PHONE(true) {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            sendMessage(context,
                    "Чтобы мы могли с вами связаться поделитесь вашим контактым телефоном.\n"
                    + "Нажмите отправить номер или введите ваш телефон в формате 0xx-xxx-xx-xx: ",
                    List.of(BotCommand.HOME_BUTTON, BotCommand.PHONE));
        }

        @Override
        public void handleInput(BotContext context) {
            if (context.getMessage().hasContact()) {
                String phoneNumber = context.getMessage().getContact().getPhoneNumber();
                context.getUser().setPhone(phoneNumber);
                log.info("Phone number updated for user={}", context.getUser().getChatId());

                next = ENTER_EMAIL;
            } else {
                String phoneNumber = context.getMessage().getText();

                if (Utils.isValidPhoneNumber(phoneNumber)) {
                    context.getUser().setPhone(phoneNumber);

                    log.info("Phone number is valid");
                    next = ENTER_EMAIL;
                } else {
                    sendMessage(context,
                            "Телефон введен с ошибками, попробуйте еще раз.",
                            List.of(BotCommand.HOME_BUTTON));

                    log.info("Phone number is not valid");
                    next = ENTER_PHONE;
                }
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    ENTER_EMAIL(true) {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Для записи Вам необходимо указать вашу электронную почту: ",
                    List.of(BotCommand.HOME_BUTTON));
        }

        @Override
        public void handleInput(BotContext context) {
            String email = context.getMessage().getText();

            if (Utils.isValidEmailAddress(email)) {
                context.getUser().setEmail(email);

                log.info("Email is valid");
                next = APPROVED;
            } else {
                sendMessage(context,
                        "Электронная почта введена с ошибками, попробуйте еще раз.",
                        List.of(BotCommand.HOME_BUTTON));

                log.info("Email is not valid");
                next = ENTER_EMAIL;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    APPROVED(true) {
        @Override
        public void enter(BotContext context) {
            sendMessage(context,
                    "Ваша заявка принята! В ближайшее время с ваши свяжется наш администратор.",
                    List.of(BotCommand.HOME_BUTTON));
        }

        @Override
        public BotState nextState() {
            return this;
        }
    };

    private static BotState[] states;   // array for states
    private final boolean inputNeeded;  // check if user need to enter something and wait for user input

    BotState(boolean inputNeeded) {
        this.inputNeeded = inputNeeded;
    }

    public static BotState getInitialState() {
        return getStateById(0);
    }

    public static BotState getStateById(int id) {
        if (states == null) {
            states = BotState.values();
        }

        return states[id];
    }

    public boolean isInputNeeded() {
        return inputNeeded;
    }

    public void handleInput(BotContext context) {
        // do nothing by default
        // handle input in state, when user send us smth
    }

    public abstract void enter(BotContext context); // change state
    public abstract BotState nextState(); // define state after current state

    public static void sendMessage(BotContext context, String text, List<String> buttonNames) {
        SendMessage message = new SendMessage();
        message.setChatId(context.getUser().getChatId());
        message.setText(text);

        try {
            if (buttonNames != null && !buttonNames.isEmpty()) {
                setButtons(message, buttonNames);
            }
            context.getBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    public static void setButtons(SendMessage sendMessage, List<String> buttonNames) {
        // Create ReplyKeyboardMarkup object
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Create a keyboard row
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        for (String buttonName : buttonNames) {
            if (buttonName.equals(BotCommand.PHONE)) {
                KeyboardButton contactButton = new KeyboardButton(buttonName);
                contactButton.setRequestContact(true);
                keyboardFirstRow.add(contactButton);
            } else {
                keyboardFirstRow.add(new KeyboardButton(buttonName));
            }
        }

        // Add the first row to the keyboard
        keyboard.add(keyboardFirstRow);
        // Set the keyboard to the markup
        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}
