package com.boroda.app.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class ChatSettingsComponent {
    private int port = 5555;
    private String nickname = "User";
    private boolean isGroupChat = false;
}
