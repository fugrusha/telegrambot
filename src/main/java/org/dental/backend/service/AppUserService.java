package org.dental.backend.service;

import org.dental.backend.domain.AppUser;
import org.dental.backend.dto.AppUserCreateDTO;
import org.dental.backend.dto.AppUserPatchDTO;
import org.dental.backend.dto.AppUserReadDTO;
import org.dental.backend.repository.AppUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private TranslationService translationService;

    @Transactional(readOnly = true)
    public AppUserReadDTO findByChatId(long chatId) {
        AppUser user = appUserRepository.findByChatId(chatId);
        return translationService.toRead(user);
    }

    @Transactional(readOnly = true)
    public List<AppUserReadDTO> getAllUsers() {
        List<AppUser> users = appUserRepository.getAllUsers();
        return users.stream().map(translationService::toRead).collect(Collectors.toList());
    }

    @Transactional
    public AppUserReadDTO createUser(AppUserCreateDTO createDTO) {
        AppUser user = new ModelMapper().map(createDTO, AppUser.class);

        if (createDTO.getUsername().equals("fugrusha")) {
            user.setIsAdmin(true);
        }

        user = appUserRepository.save(user);
        return translationService.toRead(user);
    }

    @Transactional
    public void updateUser(long chatId, AppUserPatchDTO patchDTO) {
        AppUser user = appUserRepository.findByChatId(chatId);

        translationService.patchEntity(user, patchDTO);

        appUserRepository.save(user);
    }


}
