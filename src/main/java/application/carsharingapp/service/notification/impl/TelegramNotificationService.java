package application.carsharingapp.service.notification.impl;

import application.carsharingapp.exception.NotificationSendingException;
import application.carsharingapp.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Profile("!test")
@RequiredArgsConstructor
@Service
public class TelegramNotificationService extends TelegramLongPollingBot
        implements NotificationService {
    @Value("${bot.username}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.target-chat-id}")
    private String targetChatId;

    @Override
    public void sendNotification(String message) {
        SendMessage sendMessage = new SendMessage(targetChatId, message);
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
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
