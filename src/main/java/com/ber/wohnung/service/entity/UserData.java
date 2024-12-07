package com.ber.wohnung.service.entity;

import jakarta.persistence.Entity;
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
        CONFIRMATION
    }

    public UserData() {
        this.state = RegistrationState.START;
    }
}
