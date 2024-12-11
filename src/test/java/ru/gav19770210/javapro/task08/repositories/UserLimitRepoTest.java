package ru.gav19770210.javapro.task08.repositories;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.gav19770210.javapro.task08.entities.UserLimitEntity;
import ru.gav19770210.javapro.task08.services.UserLimitServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserLimitRepoTest {
    @Autowired
    UserLimitRepo userLimitRepo;
    private UserLimitEntity userLimitEntityTest;

    @BeforeEach
    public void beforeEach() {
        userLimitEntityTest = UserLimitEntity.builder()
                .userId(123456L)
                .dailyLimit(UserLimitServiceImpl.DEFAULT_DAILY_LIMIT)
                .currDailyLimit(UserLimitServiceImpl.DEFAULT_DAILY_LIMIT)
                .currDailyLimitDate(LocalDateTime.now())
                .build();
    }

    @AfterEach
    public void afterEach() {
        userLimitRepo.deleteAll();
        userLimitEntityTest = null;
    }

    @Test
    @DisplayName("findAll. Получение всех лимитов пользователей")
    void findAllTest() {
        var userLimitEntityList = (List<UserLimitEntity>) userLimitRepo.findAll();
        var listCount = userLimitEntityList.size();
        var userCreate = userLimitRepo.save(userLimitEntityTest);
        userLimitEntityList = (List<UserLimitEntity>) userLimitRepo.findAll();

        Assertions.assertNotNull(userCreate);
        Assertions.assertEquals(listCount + 1, userLimitEntityList.size());
    }

    @Test
    @DisplayName("findById. Получение лимита пользователя с заданным <id>")
    void findByIdTest() {
        var userLimitEntityCreate = userLimitRepo.save(userLimitEntityTest);
        var userLimitEntityGet = userLimitRepo.findById(userLimitEntityTest.getId()).get();

        Assertions.assertNotNull(userLimitEntityCreate);
        Assertions.assertEquals(userLimitEntityCreate, userLimitEntityGet);
    }

    @Test
    @DisplayName("findByUserId. Получение лимита пользователя с заданным <user_id>")
    void findByUserIdTest() {
        var userLimitEntityCreate = userLimitRepo.save(userLimitEntityTest);
        var userLimitEntityGet = userLimitRepo.findByUserId(userLimitEntityTest.getUserId()).get();

        Assertions.assertNotNull(userLimitEntityCreate);
        Assertions.assertEquals(userLimitEntityCreate, userLimitEntityGet);
    }

    @Test
    @DisplayName("updateDailyLimit. Обновление лимита пользователя")
    void updateDailyLimitTest() {
        var userLimitEntityCreate = userLimitRepo.save(userLimitEntityTest);
        userLimitEntityCreate.setDailyLimit(userLimitEntityCreate.getDailyLimit().add(UserLimitServiceImpl.DEFAULT_DAILY_LIMIT));
        var userLimitEntityUpdate = userLimitRepo.save(userLimitEntityCreate);

        Assertions.assertNotNull(userLimitEntityCreate);
        Assertions.assertEquals(userLimitEntityCreate.getDailyLimit(), userLimitEntityUpdate.getDailyLimit());
    }

    @Test
    @DisplayName("deleteById. Удаление лимита пользователя с заданным <id>")
    void deleteByIdTest() {
        var userLimitEntityCreate = userLimitRepo.save(userLimitEntityTest);
        userLimitRepo.deleteById(userLimitEntityCreate.getId());
        var userLimitEntityGet = userLimitRepo.findById(userLimitEntityCreate.getId());

        Assertions.assertNotNull(userLimitEntityCreate);
        Assertions.assertTrue(userLimitEntityGet.isEmpty());
    }
}
