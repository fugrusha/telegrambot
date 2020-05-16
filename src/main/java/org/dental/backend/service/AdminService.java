package org.dental.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.dental.backend.bot.ChatBot;
import org.dental.backend.domain.AppUser;
import org.dental.backend.dto.AppUserReadDTO;
import org.dental.backend.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ChatBot chatBot;

    private static final String BROADCAST = "broadcast ";
    private static final String LIST_USERS = "users";

    public boolean checkIfAdminCommand(long chatId, String text) {
        if (text.startsWith(BROADCAST)) {
            log.info("Admin command received: " + BROADCAST);

            if (isAdmin(chatId)) {
                text = text.substring(BROADCAST.length());
                broadcast(text);
            }

            return true;

        } else if (text.equals(LIST_USERS)) {
            log.info("Admin command received: " + LIST_USERS);

            if (isAdmin(chatId)) {
                getAllUsers(chatId);
            }

            return true;
        }

        return false;
    }

    private void getAllUsers(Long adminChatId) {
        StringBuilder sb = new StringBuilder("All users: \r\n");

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

        chatBot.sendMessage(adminChatId, sb.toString(), null);
    }

    private void broadcast(String text) {
        appUserRepository.getAllChatIds().forEach(id -> chatBot.sendMessage(id, text, null));
    }

    private boolean isAdmin(long chatId) {
        AppUser user = appUserService.findByChatId(chatId);

        return user.getIsAdmin();
    }
}
