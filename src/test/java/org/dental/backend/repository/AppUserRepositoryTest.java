package org.dental.backend.repository;

import org.assertj.core.api.Assertions;
import org.dental.backend.TestObjectFactory;
import org.dental.backend.domain.AppUser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@DataJpaTest
@RunWith(SpringRunner.class)
@Import(TestObjectFactory.class)
@Sql(statements = {"delete from app_user"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private TestObjectFactory testObjectFactory;

    @Test
    public void testFindByChatId() {
        long user1ChatId = 111111111L;
        long user2ChatId = 222222222L;
        long user3ChatId = 333333333L;

        AppUser user1 = testObjectFactory.createUser(user1ChatId, 0);
        testObjectFactory.createUser(user2ChatId, 2);
        testObjectFactory.createUser(user3ChatId, 1);

        AppUser user = appUserRepository.findByChatId(user1ChatId);
        Assertions.assertThat(user).isEqualToComparingFieldByField(user1);
    }

    @Test
    public void testGetAllChatIds() {
        Set<Long> expectedResult = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            expectedResult.add(testObjectFactory.createUserWithRandomChatId().getChatId());
        }

        Set<Long> actualResult = appUserRepository.getAllChatIds().collect(Collectors.toSet());
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetAllUsers() {
        long user1ChatId = 111111111L;
        long user2ChatId = 222222222L;
        long user3ChatId = 333333333L;

        testObjectFactory.createUser(user1ChatId, 0);
        testObjectFactory.createUser(user2ChatId, 2);
        testObjectFactory.createUser(user3ChatId, 1);

        List<AppUser> actualResult = appUserRepository.getAllUsers();

        Assertions.assertThat(actualResult).extracting("chatId")
                .containsExactlyInAnyOrder(user1ChatId, user2ChatId, user3ChatId);
    }

}
