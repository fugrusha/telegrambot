package org.dental.backend.repository;

import org.dental.backend.domain.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, UUID> {

    AppUser findByChatId(long id);
}
