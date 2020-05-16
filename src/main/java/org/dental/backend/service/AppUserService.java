package org.dental.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.dental.backend.domain.AppUser;
import org.dental.backend.dto.AppUserReadDTO;
import org.dental.backend.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private TranslationService translationService;

    @Transactional(readOnly = true)
    public boolean isChatIdExists(Long chatId) {
        return appUserRepository.getAllChatIds().anyMatch(x -> Objects.equals(x, chatId));
    }

    @Transactional(readOnly = true)
    public AppUser findByChatId(long chatId) {
        return appUserRepository.findByChatId(chatId);
    }

    @Transactional(readOnly = true)
    public List<AppUserReadDTO> getAllUsers() {
        List<AppUser> users = appUserRepository.getAllUsers();
        return users.stream().map(translationService::toRead).collect(Collectors.toList());
    }

    @Transactional
    public AppUser createUser(Long chatId, String firstName, String lastName, String username) {
        AppUser user = new AppUser();
        user.setChatId(chatId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setStateId(0);

        if (chatId.equals(221802972L)) {
            user.setIsAdmin(true);
        }

        return appUserRepository.save(user);
    }

    @Transactional
    public void updateUser(AppUser user) {
        appUserRepository.save(user);
    }
}
