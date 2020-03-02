package org.dental.backend.service;

import org.dental.backend.bot.BotState;
import org.dental.backend.domain.AppUser;
import org.dental.backend.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Transactional(readOnly = true)
    public AppUser findByChatId(long chatId) {
        return appUserRepository.findByChatId(chatId);
    }

    @Transactional(readOnly = true)
    public List<AppUser> findAllUsers() {
        return (List<AppUser>) appUserRepository.findAll();
    }

    @Transactional
    public AppUser createUser(long chatId, BotState state, String firstName, String lastName) {
        AppUser user = new AppUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setChatId(chatId);
        user.setStateId(state.ordinal());

        user.setIsAdmin(appUserRepository.count() == 0);

        user = appUserRepository.save(user);
        return user;
    }

    @Transactional
    public void updateUser(AppUser user) {
        appUserRepository.save(user);
    }
}
