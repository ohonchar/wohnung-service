package com.ber.wohnung.service.entity;

import lombok.Data;

@Data
public class UserData {
    // Getters and setters
    private String name;
    private String email;
    private RegistrationState state;

    public enum RegistrationState {
        START,
        NAME,
        EMAIL,
        CONFIRMATION,
        SEARCH
    }

    public UserData() {
        this.state = RegistrationState.START;
    }
}
