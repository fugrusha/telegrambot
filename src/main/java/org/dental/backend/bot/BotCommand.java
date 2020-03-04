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
