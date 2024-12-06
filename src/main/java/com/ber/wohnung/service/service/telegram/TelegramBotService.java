package com.ber.wohnung.service.service.telegram;

import com.ber.wohnung.service.entity.UserData;
import com.ber.wohnung.service.service.selenium.RunUiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TelegramBotService implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    @Autowired
    private final RunUiService runUiService;

    private static String botToken;

    private final TelegramClient telegramClient;
    private long chatId;

    private Map<Long, UserData> registrationSession = new HashMap<>();

    public TelegramBotService(RunUiService runUiService, @Value("${spring.bot.token}") String botToken) {
        this.runUiService = runUiService;
        TelegramBotService.botToken = botToken;
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        SendMessage message = null;

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String messageText = update.getMessage().getText();
            chatId = update.getMessage().getChatId();

            if (messageText.toLowerCase().contains("execute")) {
                runUiService.executeUi(messageText.split(" ")[1]);
                messageText = "This is your UI";
            }

            if (messageText.equalsIgnoreCase("/start")) {
                message = buildReplyKeyboard(messageText, replayKeyboard());
            } else {
                message = buildMessage(messageText);
            }

            if (messageText.equalsIgnoreCase("registration") || !registrationSession.isEmpty()) {
                UserData userData = registrationSession.getOrDefault(chatId, new UserData());
                message = handleUserMessage(messageText, userData);
                registrationSession.put(chatId, userData);
            }
            executeRequest(message);
        }

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData() != null) {
            String buttonData = update.getCallbackQuery().getData();
            if (buttonData.trim().equalsIgnoreCase("registration")) {
                message = SendMessage
                        .builder()
                        .chatId(chatId)
                        .text("Update message text")
                        .replyMarkup(InlineKeyboardMarkup
                                .builder()
                                .keyboardRow(
                                        new InlineKeyboardRow(InlineKeyboardButton
                                                .builder()
                                                .text("Update message text")
                                                .callbackData("update_msg_text")
                                                .build()
                                        )
                                )
                                .build())
                        .build();
            }
            executeRequest(message);
        }
    }

    private SendMessage handleUserMessage(String userMessage, UserData userData) {

        SendMessage.SendMessageBuilder<?, ?> message = SendMessage.builder()
                .chatId(chatId);
        switch (userData.getState()) {
            case START:
                message.text("Welcome to the registration! Please enter your name:");
                userData.setState(UserData.RegistrationState.NAME);
                break;

            case NAME:
                userData.setName(userMessage);
                message.text("Great! Now, please enter your email:");
                userData.setState(UserData.RegistrationState.EMAIL);
                break;

            case EMAIL:
                userData.setEmail(userMessage);
                message.text(String.format("Please confirm your details:\n\nName: %s\nEmail: %s\n\nType 'confirm' to proceed or 'restart' to start over.",
                        userData.getName(), userData.getEmail()));
                userData.setState(UserData.RegistrationState.CONFIRMATION);
                break;

            case CONFIRMATION:
                if (userMessage.equalsIgnoreCase("confirm")) {
                    message.text("Thank you for registering! Your details have been saved.");
                    registrationSession.remove(chatId); // Clear session after successful registration
//                    registrationSession = new HashMap<>();
                } else if (userMessage.equalsIgnoreCase("restart")) {
                    message.text("Let's start over! Please enter your name:");
                    userData.setState(UserData.RegistrationState.NAME);
                } else {
                    message.text("Invalid response. Type 'confirm' to proceed or 'restart' to start over.");
                }
                break;
        }
        return message.build();
    }

    private ReplyKeyboardMarkup replayKeyboard() {
        // Create rows for the keyboard
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("registration"));
        row1.add(new KeyboardButton("Option 2"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Option 3"));
        row2.add(new KeyboardButton("Option 4"));

        // Combine rows into a keyboard
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);

        // Build the ReplyKeyboardMarkup
        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .inputFieldPlaceholder("hellosasha")
                .resizeKeyboard(true) // Optional: Resize the keyboard
                .oneTimeKeyboard(true) // Optional: Hide after use
                .build();
    }


    private InlineKeyboardMarkup mainMenu() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton
                        .builder()
                        .text("Row 1 Column 1")
                        .callbackData("Row 1 Column 1")
                        .build(),
                        InlineKeyboardButton
                                .builder()
                                .text("Row 1 Column 2")
                                .callbackData("Row 1 Column 2")
                                .build())
                )
                .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton
                        .builder()
                        .text("Row 2 Column 1")
                        .callbackData("Row 2 Column 1")
                        .build(),
                        InlineKeyboardButton
                                .builder()
                                .text("New registration")
                                .callbackData("registration")
                                .build())
                )
                .build();
    }

    private SendMessage buildMessage(String messageText) {
        return SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(messageText)
                .build();
    }

    private SendMessage buildKeyboard(String messageText, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(messageText)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    private SendMessage buildReplyKeyboard(String messageText, ReplyKeyboardMarkup replyKeyboardMarkup) {
        return SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(messageText)
                .replyMarkup(replyKeyboardMarkup)
                .build();
    }

    private void executeRequest(SendMessage message) {
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }
}
