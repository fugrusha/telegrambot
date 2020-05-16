package org.dental.backend.service;

import org.dental.backend.domain.AppUser;
import org.dental.backend.dto.AppUserReadDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    public AppUserReadDTO toRead(AppUser user) {
        return new ModelMapper().map(user, AppUserReadDTO.class);
    }
}
