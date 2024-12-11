package ru.gav19770210.javapro.task08.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gav19770210.javapro.task08.entities.UserLimitEntity;
import ru.gav19770210.javapro.task08.repositories.UserLimitRepo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserLimitServiceTest {
    @Mock
    UserLimitRepo userLimitRepo;
    @InjectMocks
    UserLimitServiceImpl userLimitService;
    private UserLimitEntity userLimitEntityTest;

    @BeforeEach
    public void beforeEach() {
        userLimitEntityTest = UserLimitEntity.builder()
                .id(654321L)
                .userId(123456L)
                .dailyLimit(UserLimitServiceImpl.DEFAULT_DAILY_LIMIT)
                .currDailyLimit(UserLimitServiceImpl.DEFAULT_DAILY_LIMIT.multiply(BigDecimal.valueOf(0.5)))
                .currDailyLimitDate(LocalDateTime.now())
                .build();
    }

    @AfterEach
    public void afterEach() {
        userLimitEntityTest = null;
    }

    @Test
    @DisplayName("getLimit. Получение лимита пользователя")
    public void getLimitTest() {

        Mockito.when(userLimitRepo.findByUserId(userLimitEntityTest.getUserId()))
                .thenReturn(Optional.of(userLimitEntityTest));

        var userLimitEntityGet = userLimitService.getLimit(userLimitEntityTest.getUserId());

        Assertions.assertEquals(userLimitEntityTest, userLimitEntityGet);

        Mockito.verify(userLimitRepo).findByUserId(userLimitEntityTest.getUserId());
    }

    @Test
    @DisplayName("getLimit. Проверка на отсутствие лимита пользователя")
    public void getLimitTest_NotExistByUserId() {

        Mockito.when(userLimitRepo.findByUserId(userLimitEntityTest.getUserId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class,
                () -> userLimitService.getLimit(userLimitEntityTest.getUserId()),
                "Не сработала проверка на отсутствие лимита пользователя с заданным <user_id>");

        Mockito.verify(userLimitRepo).findByUserId(userLimitEntityTest.getUserId());
    }

    @Test
    @DisplayName("setDailyLimit. Уменьшение дневного лимита пользователя")
    public void setDailyLimitTest_Dec() {

        var decDailyLimit = UserLimitServiceImpl.DEFAULT_DAILY_LIMIT.multiply(BigDecimal.valueOf(0.5));
        var newDailyLimit = userLimitEntityTest.getDailyLimit().subtract(decDailyLimit);
        var newCurrDailyLimit = userLimitEntityTest.getCurrDailyLimit().subtract(decDailyLimit);

        var userLimitEntitySave = UserLimitEntity.builder()
                .id(userLimitEntityTest.getId())
                .userId(userLimitEntityTest.getUserId())
                .dailyLimit(newDailyLimit)
                .currDailyLimit(newCurrDailyLimit)
                .currDailyLimitDate(userLimitEntityTest.getCurrDailyLimitDate())
                .build();

        Mockito.when(userLimitRepo.findByUserId(userLimitEntityTest.getUserId()))
                .thenReturn(Optional.of(userLimitEntityTest));

        Mockito.when(userLimitRepo.save(userLimitEntityTest))
                .thenReturn(userLimitEntitySave);

        var userLimitEntityGet = userLimitService.setDailyLimit(userLimitEntityTest.getUserId(), newDailyLimit);

        Assertions.assertEquals(userLimitEntityTest, userLimitEntityGet);

        Mockito.verify(userLimitRepo).findByUserId(userLimitEntityTest.getUserId());
        Mockito.verify(userLimitRepo).save(userLimitEntityTest);
    }

    @Test
    @DisplayName("setDailyLimit. Увеличение дневного лимита пользователя")
    public void setDailyLimitTest_Inc() {

        var incDailyLimit = UserLimitServiceImpl.DEFAULT_DAILY_LIMIT;
        var newDailyLimit = userLimitEntityTest.getDailyLimit().add(incDailyLimit);
        var newCurrDailyLimit = userLimitEntityTest.getCurrDailyLimit().add(incDailyLimit);

        var userLimitEntitySave = UserLimitEntity.builder()
                .id(userLimitEntityTest.getId())
                .userId(userLimitEntityTest.getUserId())
                .dailyLimit(newDailyLimit)
                .currDailyLimit(newCurrDailyLimit)
                .currDailyLimitDate(userLimitEntityTest.getCurrDailyLimitDate())
                .build();

        Mockito.when(userLimitRepo.findByUserId(userLimitEntityTest.getUserId()))
                .thenReturn(Optional.of(userLimitEntityTest));

        Mockito.when(userLimitRepo.save(userLimitEntityTest))
                .thenReturn(userLimitEntitySave);

        var userLimitEntityGet = userLimitService.setDailyLimit(userLimitEntityTest.getUserId(), newDailyLimit);

        Assertions.assertEquals(userLimitEntityTest, userLimitEntityGet);

        Mockito.verify(userLimitRepo).findByUserId(userLimitEntityTest.getUserId());
        Mockito.verify(userLimitRepo).save(userLimitEntityTest);
    }

    @Test
    @DisplayName("decreaseLimit. Уменьшение текущего дневного лимита пользователя")
    public void decreaseLimitTest() {

        var decAmount = userLimitEntityTest.getCurrDailyLimit().multiply(BigDecimal.valueOf(0.2));
        var newCurrDailyLimit = userLimitEntityTest.getCurrDailyLimit().subtract(decAmount);

        var userLimitEntitySave = UserLimitEntity.builder()
                .id(userLimitEntityTest.getId())
                .userId(userLimitEntityTest.getUserId())
                .dailyLimit(userLimitEntityTest.getDailyLimit())
                .currDailyLimit(newCurrDailyLimit)
                .currDailyLimitDate(userLimitEntityTest.getCurrDailyLimitDate())
                .build();

        Mockito.when(userLimitRepo.findByUserId(userLimitEntityTest.getUserId()))
                .thenReturn(Optional.of(userLimitEntityTest));

        Mockito.when(userLimitRepo.save(userLimitEntityTest))
                .thenReturn(userLimitEntitySave);

        var userLimitEntityGet = userLimitService.decreaseLimit(userLimitEntityTest.getUserId(), decAmount);
        userLimitEntityTest.setCurrDailyLimitDate(userLimitEntityGet.getCurrDailyLimitDate());

        Assertions.assertEquals(userLimitEntityTest, userLimitEntityGet);

        Mockito.verify(userLimitRepo).findByUserId(userLimitEntityTest.getUserId());
        Mockito.verify(userLimitRepo).save(userLimitEntityTest);
    }

    @Test
    @DisplayName("decreaseLimit. Уменьшение текущего дневного лимита пользователя на сумму больше остатка лимита")
    public void decreaseLimitTest_NotEnough() {

        var decAmount = userLimitEntityTest.getCurrDailyLimit().multiply(BigDecimal.valueOf(1.2));

        Mockito.when(userLimitRepo.findByUserId(userLimitEntityTest.getUserId()))
                .thenReturn(Optional.of(userLimitEntityTest));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userLimitService.decreaseLimit(userLimitEntityTest.getUserId(), decAmount),
                "Не сработала проверка на превышение лимита пользователя");

        Mockito.verify(userLimitRepo).findByUserId(userLimitEntityTest.getUserId());
    }

    @Test
    @DisplayName("increaseLimit. Увеличение текущего дневного лимита пользователя")
    public void increaseLimitTest() {

        var incAmount = userLimitEntityTest.getCurrDailyLimit().multiply(BigDecimal.valueOf(0.2));
        var newCurrDailyLimit = userLimitEntityTest.getCurrDailyLimit().add(incAmount);

        var userLimitEntitySave = UserLimitEntity.builder()
                .id(userLimitEntityTest.getId())
                .userId(userLimitEntityTest.getUserId())
                .dailyLimit(userLimitEntityTest.getDailyLimit())
                .currDailyLimit(newCurrDailyLimit)
                .currDailyLimitDate(userLimitEntityTest.getCurrDailyLimitDate())
                .build();

        Mockito.when(userLimitRepo.findByUserId(userLimitEntityTest.getUserId()))
                .thenReturn(Optional.of(userLimitEntityTest));

        Mockito.when(userLimitRepo.save(userLimitEntityTest))
                .thenReturn(userLimitEntitySave);

        var userLimitEntityGet = userLimitService.increaseLimit(userLimitEntityTest.getUserId(), incAmount);
        userLimitEntityTest.setCurrDailyLimitDate(userLimitEntityGet.getCurrDailyLimitDate());

        Assertions.assertEquals(userLimitEntityTest, userLimitEntityGet);

        Mockito.verify(userLimitRepo).findByUserId(userLimitEntityTest.getUserId());
        Mockito.verify(userLimitRepo).save(userLimitEntityTest);
    }

    @Test
    @DisplayName("increaseLimit. Увеличение текущего дневного лимита пользователя выше номинала")
    public void increaseLimitTest_MoreThenNominal() {

        var incAmount = userLimitEntityTest.getCurrDailyLimit().multiply(BigDecimal.valueOf(1.2));
        var newCurrDailyLimit = userLimitEntityTest.getDailyLimit();

        var userLimitEntitySave = UserLimitEntity.builder()
                .id(userLimitEntityTest.getId())
                .userId(userLimitEntityTest.getUserId())
                .dailyLimit(userLimitEntityTest.getDailyLimit())
                .currDailyLimit(newCurrDailyLimit)
                .currDailyLimitDate(userLimitEntityTest.getCurrDailyLimitDate())
                .build();

        Mockito.when(userLimitRepo.findByUserId(userLimitEntityTest.getUserId()))
                .thenReturn(Optional.of(userLimitEntityTest));

        Mockito.when(userLimitRepo.save(userLimitEntityTest))
                .thenReturn(userLimitEntitySave);

        var userLimitEntityGet = userLimitService.increaseLimit(userLimitEntityTest.getUserId(), incAmount);
        userLimitEntityTest.setCurrDailyLimitDate(userLimitEntityGet.getCurrDailyLimitDate());

        Assertions.assertEquals(userLimitEntityTest, userLimitEntityGet);

        Mockito.verify(userLimitRepo).findByUserId(userLimitEntityTest.getUserId());
        Mockito.verify(userLimitRepo).save(userLimitEntityTest);
    }
}
