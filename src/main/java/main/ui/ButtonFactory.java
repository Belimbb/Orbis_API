package main.ui;

import main.systemSettings.AppRegistry;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
public class ButtonFactory {

    // Method for creating a persistent user keyboard
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

    // Method for creating an inline keyboard based on provided parameters
    public static InlineKeyboardMarkup createKeyboardForCriteriaSelection(Long chatId, Map<String, String> options, String prefix) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        Map<String, String> selectedCriteria = AppRegistry.getUser(chatId).getAllSearchCriteria();

        for (Map.Entry<String, String> option : options.entrySet()) {
            boolean isSelected = selectedCriteria.containsKey(option.getKey());
            String buttonText = isSelected ? "\u2705 " + option.getValue() : option.getValue();
            buttons.add(createButton(buttonText, prefix + "_" + option.getKey()));
        }
        buttons.add(createButton("Submit criteria", prefix + "_" + "submit"));
        return buildInlineKeyboard(buttons);
    }
    public static InlineKeyboardMarkup createUniversalInlineKeyboard(Map<String, String> buttonsData) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        for (Map.Entry<String, String> entry : buttonsData.entrySet()) {
            String buttonText = entry.getValue();
            String callbackData = entry.getKey();
            buttons.add(createButton(buttonText, callbackData));
        }

        return buildInlineKeyboard(buttons);
    }

    // Auxiliary method for creating a button
    private static InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    // Auxiliary method for building a keyboard from a list of buttons
    private static InlineKeyboardMarkup buildInlineKeyboard(List<InlineKeyboardButton> buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (InlineKeyboardButton button : buttons) {
            currentRow.add(button);
            if (currentRow.size() == 3) {
                keyboard.add(new ArrayList<>(currentRow));
                currentRow.clear();
            }
        }

        if (!currentRow.isEmpty()) {
            keyboard.add(currentRow);
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}