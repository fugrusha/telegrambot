package org.dental.backend.repository;

import org.dental.backend.domain.AppUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, UUID> {

    AppUser findByChatId(Long id);

    @Query("select u.id from AppUser u")
    Stream<Long> getAllChatIds();

    @Query("from AppUser")
    List<AppUser> getAllUsers();

}
