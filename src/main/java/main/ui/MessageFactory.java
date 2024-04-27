package main.ui;

import lombok.AllArgsConstructor;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/**
 * MVC: View (Class helper)
 * @author AlekseyB belovmladshui@gmail.com
 */
@AllArgsConstructor
public class MessageFactory {
    private final Long chatId;

    // Creating a plain text message
    public SendMessage createMessage(String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode(ParseMode.HTML);
        return message;
    }

    // Creating a text message with emoji
    public SendMessage createMessageWithEmoji(String emoji, String text) {
        String fullText = emoji.isEmpty() ? text : emoji + " " + text;
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(fullText);
        message.setParseMode(ParseMode.HTML);
        return message;
    }

    // Creating a message with a photo
    public SendPhoto createPhotoMessage(String imageUrl, String caption) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setPhoto(new InputFile(imageUrl));
        photo.setCaption(caption);
        photo.setParseMode(ParseMode.HTML);
        return photo;
    }

    // Edit an existing message
    public EditMessageText createEditMessage(Long chatId, Integer messageId, String messageText) {
        return createEditMessage(chatId, messageId, "", messageText);
    }

    public EditMessageText createEditMessage(Long chatId, Integer messageId, String emoji, String messageText) {
        EditMessageText newMessage = new EditMessageText();
        newMessage.setChatId(String.valueOf(chatId));
        newMessage.setMessageId(messageId);
        newMessage.setText(emoji.isEmpty() ? messageText : emoji + "  " + messageText);
        newMessage.setParseMode(ParseMode.HTML);
        return newMessage;
    }
}
