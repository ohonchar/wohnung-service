package com.ber.wohnung.service.service.telegram;

import com.ber.wohnung.service.entity.Employee;
import com.ber.wohnung.service.entity.UserData;
import com.ber.wohnung.service.service.EmployeeService;
import com.ber.wohnung.service.service.selenium.RunUiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.ber.wohnung.service.service.telegram.TelegramBotService.registrationSession;

@Component
public class UserRegistration {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RunUiService runUiService;

    public SendMessage handleUserMessage(long chatId, String userMessage, UserData userData, TelegramBotService telegramBotService) {

        SendMessage.SendMessageBuilder<?, ?> message = SendMessage.builder()
                .chatId(chatId);
        switch (userData.getState()) {
            case START:
                message.text("""
                        Welcome to the registration!
                        Please enter your name:
                        """);
                userData.setState(UserData.RegistrationState.NAME);
                registrationSession.put(chatId, userData);
                break;

            case NAME:
                userData.setName(userMessage);
                message.text("""
                        Great!
                        Now, please enter your email:
                        """);
                userData.setState(UserData.RegistrationState.EMAIL);
                registrationSession.put(chatId, userData);
                break;

            case EMAIL:
                if (isValidEmail(userMessage)) {
                    userData.setEmail(userMessage);
                    String messageText = String.format("""
                                    Please confirm your details:
                                    
                                    Name: %s
                                    Email: %s
                                    
                                    Click 'Confirm' to proceed or 'Restart' to start over.
                                    """,
                            userData.getName(), userData.getEmail());
                    message
                            .text(messageText)
                            .replyMarkup(confirmationKeyboard());
                    userData.setState(UserData.RegistrationState.CONFIRMATION);
                    registrationSession.put(chatId, userData);
                } else {
                    message.text("""
                            That doesn't look like a valid email.
                            Please try again:
                            """);
                }
                break;

            case CONFIRMATION:
                if (userMessage.equalsIgnoreCase("confirm")) {
                    Employee employee = new Employee();
                    employee.setFirstName(userData.getName());
                    employee.setEmail(userData.getEmail());
                    employeeService.saveEmployee(employee);

                    message
                            .text("""
                                    Your details have been saved!
                                    Wish you to start search?
                                    """)
                            .replyMarkup(executeSearchKeyboard());
                    userData.setState(UserData.RegistrationState.SEARCH);
                } else if (userMessage.equalsIgnoreCase("restart")) {
                    message.text("Let's start over! Please enter your name:");
                    userData.setState(UserData.RegistrationState.NAME);
                } else {
                    message.text("Invalid response. Type 'confirm' to proceed or 'restart' to start over.");
                }
                break;

            case SEARCH:
                if (userMessage.equalsIgnoreCase("search")) {
                    message.text("Start searching");
                    runUiService.executeUi(userData.getEmail());
                }
                registrationSession.remove(chatId);
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

    private ReplyKeyboardMarkup confirmationKeyboard() {
        // Create rows for the keyboard
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Confirm"));
        row1.add(new KeyboardButton("Restart"));

        // Combine rows into a keyboard
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);

        // Build the ReplyKeyboardMarkup
        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .resizeKeyboard(true) // Optional: Resize the keyboard
                .oneTimeKeyboard(true) // Optional: Hide after use
                .build();
    }

    private ReplyKeyboardMarkup executeSearchKeyboard() {
        // Create rows for the keyboard
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Search"));
        row1.add(new KeyboardButton("Cancel"));

        // Combine rows into a keyboard
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);

        // Build the ReplyKeyboardMarkup
        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .resizeKeyboard(true) // Optional: Resize the keyboard
                .oneTimeKeyboard(true) // Optional: Hide after use
                .build();
    }
}
