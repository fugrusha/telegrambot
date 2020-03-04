package org.dental.backend.service;

import org.dental.backend.bot.ChatBot;
import org.dental.backend.dto.AppUserReadDTO;
import org.dental.backend.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ChatBot chatBot;

    public void getAllUsers(AppUserReadDTO admin) {
        StringBuilder sb = new StringBuilder("All user list: \r\n");

        List<AppUserReadDTO> users = appUserService.getAllUsers();

        users.forEach(user ->
                sb.append(user.getFirstName())
                        .append(" ")
                        .append(user.getLastName())
                        .append(" | тел.")
                        .append(user.getPhone())
                        .append(" | ")
                        .append(user.getEmail())
                        .append(" | Статус: ")
                        .append(user.getStateId())
                        .append("\r\n")
        );

        chatBot.sendMessage(admin.getChatId(), sb.toString());
    }

    public void broadcast(String text) {
        appUserRepository.getAllChatIds().forEach(id -> chatBot.sendMessage(id, text));
    }
}
