package org.dental.backend.bot;

import org.dental.backend.utils.Utils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public enum BotState {

    START {
        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Для продолжения записи Вам необходимо выполнить (завершить) регистрацию.");
        }

        @Override
        public BotState nextState() {
            return ENTER_PHONE;
        }
    },

    ENTER_PHONE {
        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Для продолжения записи Вам необходимо выполнить (завершить) регистрацию.\n"
                    + "Введите ваш телефон в формате 0xx-xxx-xx-xx: ");
        }

        @Override
        public void handleInput(BotContext context) {
            context.getUser().setPhone(context.getInput());
        }

        @Override
        public BotState nextState() {
            return ENTER_EMAIL;
        }
    },

    ENTER_EMAIL {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Для продолжения записи Вам необходимо выполнить (завершить) регистрацию.\n"
                    + "Введите вашу электронную почту: ");
        }

        @Override
        public void handleInput(BotContext context) {
            String email = context.getInput();

            if (Utils.isValidEmailAddress(email)) {
                context.getUser().setEmail(context.getInput());
                next = APPROVED;
            } else {
                sendMessage(context, "Электронная почта введена с ошибками, попробуйте еще раз. \n"
                + "Это последний шаг, я обещаю :)");
                next = ENTER_EMAIL;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    APPROVED(false) {
        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Ваша заявка принята! В ближайшее время с ваши свяжется наш администратор.");
        }

        @Override
        public BotState nextState() {
            return BotState.getInitialState();
        }
    };


    private static BotState[] states;   // array for states
    private final boolean inputNeeded;  // check if user need to enter something and wait for user input

    BotState() {
        this.inputNeeded = true;
    }

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

    public synchronized void sendMessage(BotContext context, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(context.getUser().getChatId());
        message.setText(text);

        try {
            context.getBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
}
