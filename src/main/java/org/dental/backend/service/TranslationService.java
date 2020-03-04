package org.dental.backend.service;

import org.dental.backend.domain.AppUser;
import org.dental.backend.dto.AppUserPatchDTO;
import org.dental.backend.dto.AppUserReadDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    public AppUserReadDTO toRead(AppUser user) {
        return new ModelMapper().map(user, AppUserReadDTO.class);
    }

    public void patchEntity(AppUser user, AppUserPatchDTO patchDTO) {
        if (patchDTO.getStateId() != null) {
            user.setStateId(patchDTO.getStateId());
        }
        if (patchDTO.getEmail() != null) {
            user.setEmail(patchDTO.getEmail());
        }
        if (patchDTO.getPhone() != null) {
            user.setPhone(patchDTO.getPhone());
        }
    }
}
