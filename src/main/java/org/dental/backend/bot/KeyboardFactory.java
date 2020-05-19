package org.dental.backend.bot;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardFactory {

    public static final String CONTACT_BUTTON = "Как добраться?";
    public static final String VISIT_BUTTON = "Записаться";
    public static final String HOME_BUTTON = "В главное меню";
    public static final String PHONE = "Отправить мой номер";
    public static final String QUESTION_BUTTON = "Задать вопрос";
    public static final String WRITE_BUTTON = "Написать";
    public static final String CALL_BUTTON = "Позвонить";

    public static final String CONTACT_BUTTON_EMOJI = EmojiParser.parseToUnicode(":hospital: " + CONTACT_BUTTON);
    public static final String VISIT_BUTTON_EMOJI = EmojiParser.parseToUnicode(VISIT_BUTTON + " :pencil2:");
    public static final String HOME_BUTTON_EMOJI = EmojiParser.parseToUnicode(HOME_BUTTON + ":arrow_right:");
    public static final String PHONE_EMOJI = EmojiParser.parseToUnicode(PHONE + " :iphone:");
    public static final String QUESTION_BUTTON_EMOJI = EmojiParser.parseToUnicode(QUESTION_BUTTON + ":question:");
    public static final String WRITE_BUTTON_EMOJI = EmojiParser.parseToUnicode(WRITE_BUTTON + " :email:");
    public static final String CALL_BUTTON_EMOJI = EmojiParser.parseToUnicode(CALL_BUTTON + " :calling:");

    public static ReplyKeyboard getButtons(List<String> buttonNames) {
        // Create ReplyKeyboardMarkup object
        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setSelective(true);
        replyKeyboard.setResizeKeyboard(true);
        replyKeyboard.setOneTimeKeyboard(false);

        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Create a keyboard row
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        if (buttonNames == null || buttonNames.isEmpty()) {
            keyboardFirstRow.add(new KeyboardButton(CONTACT_BUTTON));
            keyboardFirstRow.add(new KeyboardButton(QUESTION_BUTTON));
            keyboardFirstRow.add(new KeyboardButton(VISIT_BUTTON));
        } else {
            for (String buttonName : buttonNames) {
                if (buttonName.equals(PHONE)) {
                    KeyboardButton contactButton = new KeyboardButton()
                            .setText(buttonName)
                            .setRequestContact(true);

                    keyboardFirstRow.add(contactButton);
                } else {
                    keyboardFirstRow.add(new KeyboardButton(buttonName));
                }
            }
        }

        // Add the first row to the keyboard
        keyboard.add(keyboardFirstRow);
        // Set the keyboard to the markup
        replyKeyboard.setKeyboard(keyboard);
        return replyKeyboard;
    }

    public static ReplyKeyboard getContactsButtons() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        rowInline.add(new InlineKeyboardButton()
                .setText(WRITE_BUTTON_EMOJI)
                .setUrl("https://t.me/fugrusha"));

        rowInline.add(new InlineKeyboardButton()
                .setText(CALL_BUTTON_EMOJI)
                .setCallbackData(CALL_BUTTON));

        rowsInline.add(rowInline);
        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }
}
