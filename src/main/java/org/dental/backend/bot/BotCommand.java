package org.dental.backend.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class BotCommand {

    public static final String CONTACT_BUTTON = "Как добраться?";
    public static final String VISIT_BUTTON = "Запись";
    public static final String HOME_BUTTON = "В главное меню";
    public static final String PHONE = "Отправить мой номер";

    public void setButtons(SendMessage sendMessage, List<String> buttonNames) {
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
        if (buttonNames == null || buttonNames.isEmpty()) {
            keyboardFirstRow.add(new KeyboardButton(CONTACT_BUTTON));
            keyboardFirstRow.add(new KeyboardButton(VISIT_BUTTON));
        } else {
            for (String buttonName : buttonNames) {
                if (buttonName.equals(PHONE)) {
                    KeyboardButton contactButton = new KeyboardButton(buttonName);
                    contactButton.setRequestContact(true);
                    keyboardFirstRow.add(contactButton);
                } else {
                    keyboardFirstRow.add(new KeyboardButton(buttonName));
                }
            }
        }

        // Add the first row to the keyboard
        keyboard.add(keyboardFirstRow);
        // Set the keyboard to the markup
        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}
