package org.dental.backend;

import org.dental.backend.domain.AppUser;
import org.dental.backend.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TestObjectFactory {

    @Autowired
    private AppUserRepository appUserRepository;

    public AppUser createUser(long chatId, int stateId) {
        AppUser user = new AppUser();
        user.setUsername("username");
        user.setLastName("lastName");
        user.setFirstName("firstName");
        user.setChatId(chatId);
        user.setStateId(stateId);
        return appUserRepository.save(user);
    }

    public AppUser createUserWithRandomChatId() {
        AppUser user = new AppUser();
        user.setUsername("username");
        user.setLastName("lastName");
        user.setFirstName("firstName");
        user.setChatId(new Random().nextLong());
        user.setStateId(new Random().nextInt(3));
        return appUserRepository.save(user);
    }
}
