package com.ber.wohnung.service.service.telegram;

import com.ber.wohnung.service.entity.UserData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.ber.wohnung.service.service.telegram.TelegramBotService.registrationSession;

@Component
public class UserRegistration {

    public SendMessage handleUserMessage(long chatId, String userMessage, UserData userData) {

        SendMessage.SendMessageBuilder<?, ?> message = SendMessage.builder()
                .chatId(chatId);
        switch (userData.getState()) {
            case START:
                message.text("Welcome to the registration! Please enter your name:");
                userData.setState(UserData.RegistrationState.NAME);
                registrationSession.put(chatId, userData);
                break;

            case NAME:
                userData.setName(userMessage);
                message.text("Great! Now, please enter your email:");
                userData.setState(UserData.RegistrationState.EMAIL);
                registrationSession.put(chatId, userData);
                break;

            case EMAIL:
                if (isValidEmail(userMessage)) {
                    userData.setEmail(userMessage);
                    message.text(String.format("Please confirm your details:\n\nName: %s\nEmail: %s\n\nType 'confirm' to proceed or 'restart' to start over.",
                            userData.getName(), userData.getEmail()));
                    userData.setState(UserData.RegistrationState.CONFIRMATION);
                    registrationSession.put(chatId, userData);
                } else {
                    message.text("That doesn't look like a valid email. Please try again:");
                }
                break;

            case CONFIRMATION:
                if (userMessage.equalsIgnoreCase("confirm")) {
                    message.text("Thank you for registering! Your details have been saved.");
                    registrationSession.remove(chatId); // Clear session after successful registration
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


    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10,15}");
    }
}
