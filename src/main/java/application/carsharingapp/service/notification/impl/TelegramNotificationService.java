package application.carsharingapp.service.notification.impl;

import application.carsharingapp.exception.NotificationSendingException;
import application.carsharingapp.service.notification.NotificationService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
@Service
public class TelegramNotificationService extends TelegramLongPollingBot
        implements NotificationService {
    private static final Dotenv DOTENV = Dotenv.configure().load();
    private static final String BOT_USERNAME = DOTENV.get("BOT_USERNAME");
    private static final String BOT_TOKEN = DOTENV.get("BOT_TOKEN");
    private static final String TARGET_CHAT_ID = DOTENV.get("TARGET_CHAT_ID");

    @Override
    public void sendNotification(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(TARGET_CHAT_ID);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new NotificationSendingException("Can't send notification "
                    + "to chat: " + sendMessage.getChatId(), e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
