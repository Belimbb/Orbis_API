package main.ui;

import main.systemSettings.AppRegistry;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.nio.charset.StandardCharsets;
import java.util.*;
/**
 * MVC: View (Class helper)
 * @author AlekseyB belovmladshui@gmail.com
 */
public class ButtonFactory {

    // Метод для создания постоянной клавиатуры пользователя
    public static ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Get Information");
        row.add("Settings");

        keyboard.add(row);
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }

    // Метод для создания инлайн-клавиатуры на основе предоставленных параметров
    public static InlineKeyboardMarkup getInlineKeyboardMarkup(long chatId, Map<String, String> options, String prefix) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();
        Map<String, String> userCriteria = AppRegistry.getUser(chatId).getAllSearchCriteria();

        for (Map.Entry<String, String> option : options.entrySet()) {
            String buttonText = userCriteria.containsKey(option.getKey()) ? "\u2705" + option.getValue() : option.getValue();
            currentRow.add(createButton(buttonText, prefix + "_" + option.getKey()));

            // Когда в ряду наберется 3 кнопки, добавляем ряд в список и начинаем новый ряд
            if (currentRow.size() == 3) {
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            }
        }

        // Добавляем последний ряд, если он не пустой
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    /**
     * Need in future
     */
    /*
    public static InlineKeyboardMarkup getInlineKeyboardMarkup(Map<String, String> options, String prefix, List<String> userSelections) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (Map.Entry<String, String> option : options.entrySet()) {
            String buttonText = userSelections.contains(option.getKey()) ? "✅ " + option.getValue() : option.getValue();
            buttons.add(createButton(buttonText, prefix + "_" + option.getKey()));
        }
        return buildInlineKeyboard(buttons);
    }
    */
    // Вспомогательный метод для создания кнопки
    private static InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    // Вспомогательный метод для построения клавиатуры из списка кнопок
    private static InlineKeyboardMarkup buildInlineKeyboard(List<InlineKeyboardButton> buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (InlineKeyboardButton button : buttons) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
