package ru.gav19770210.javapro.task08.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gav19770210.javapro.task08.entities.UserLimitEntity;
import ru.gav19770210.javapro.task08.repositories.UserLimitRepo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class UserLimitServiceImpl implements UserLimitService {
    public static final String msgLimitNotFoundByUserId = "Не найден лимит для пользователя с ИД = %d";
    public static final String msgDailyLimitNotEnough = "Недостаточно дневного лимита для выполнения операции на сумму = %f";
    public static final BigDecimal DEFAULT_DAILY_LIMIT = new BigDecimal(10000);
    private final UserLimitRepo userLimitRepo;

    @Autowired
    public UserLimitServiceImpl(UserLimitRepo userLimitRepo) {
        this.userLimitRepo = userLimitRepo;
    }

    @Override
    public UserLimitEntity getLimit(Long userId) {
        var userLimitEntityOptional = userLimitRepo.findByUserId(userId);

        if (userLimitEntityOptional.isEmpty()) {
            throw new NoSuchElementException(String.format(msgLimitNotFoundByUserId, userId));
        } else {
            var userLimitEntity = userLimitEntityOptional.get();
            var currDailyLimitDate = userLimitEntity.getCurrDailyLimitDate();
            // Если дата последнего изменения лимита не равна текущей дате, то восстановление лимита
            if (currDailyLimitDate != null && !currDailyLimitDate.toLocalDate().isEqual(LocalDate.now())) {
                userLimitEntity.setCurrDailyLimit(userLimitEntity.getDailyLimit());
                userLimitEntity.setCurrDailyLimitDate(LocalDateTime.now());
            }
            return userLimitEntity;
        }
    }

    private UserLimitEntity getLimitDef(Long userId) {
        try {
            return getLimit(userId);

        } catch (NoSuchElementException exception) {
            // Если лимит не найден, то создание с умолчательными значениями
            return UserLimitEntity.builder()
                    .userId(userId)
                    .dailyLimit(DEFAULT_DAILY_LIMIT)
                    .currDailyLimit(DEFAULT_DAILY_LIMIT)
                    .build();
        }
    }

    @Override
    @Transactional
    public UserLimitEntity setDailyLimit(Long userId, BigDecimal dailyLimit) {
        var userLimitEntity = getLimitDef(userId);

        // Если сумма дневного лимита не передана, то значение по умолчанию
        var newDailyLimit = DEFAULT_DAILY_LIMIT;
        if (dailyLimit != null) {
            newDailyLimit = dailyLimit;
        }
        if (newDailyLimit.compareTo(userLimitEntity.getDailyLimit()) != 0) {
            var diffDailyLimit = newDailyLimit.subtract(userLimitEntity.getDailyLimit());

            userLimitEntity.setDailyLimit(newDailyLimit);
            userLimitEntity.setCurrDailyLimit(userLimitEntity.getCurrDailyLimit().add(diffDailyLimit));

            return userLimitRepo.save(userLimitEntity);
        } else {
            if (userLimitEntity.getId() != null) {
                return userLimitEntity;
            } else {
                return userLimitRepo.save(userLimitEntity);
            }
        }
    }

    @Override
    @Transactional
    public UserLimitEntity decreaseLimit(Long userId, BigDecimal amount) {
        var userLimitEntity = getLimitDef(userId);

        // Если суммы уменьшения сумма не больше текущего дневного лимита, то уменьшение
        if (userLimitEntity.getCurrDailyLimit().compareTo(amount) >= 0) {
            userLimitEntity.setCurrDailyLimit(userLimitEntity.getCurrDailyLimit().subtract(amount));
            userLimitEntity.setCurrDailyLimitDate(LocalDateTime.now());

            return userLimitRepo.save(userLimitEntity);
        } else {
            throw new IllegalArgumentException(String.format(msgDailyLimitNotEnough, amount));
        }
    }

    @Override
    @Transactional
    public UserLimitEntity increaseLimit(Long userId, BigDecimal amount) {
        var userLimitEntity = getLimitDef(userId);

        var newCurrDailyLimit = userLimitEntity.getCurrDailyLimit().add(amount);
        // Если сумма текущего дневного лимита со суммой увеличения больше номинального значения, то увеличение до номинала
        if (newCurrDailyLimit.compareTo(userLimitEntity.getDailyLimit()) > 0) {
            newCurrDailyLimit = userLimitEntity.getDailyLimit();
        }

        userLimitEntity.setCurrDailyLimit(newCurrDailyLimit);
        userLimitEntity.setCurrDailyLimitDate(LocalDateTime.now());

        return userLimitRepo.save(userLimitEntity);
    }

    @Override
    @Transactional
    @Scheduled(cron = "@daily")
    public void restoreAllCurrDailyLimit() {
        // Восстановление текущего дневного лимита до номинала
        userLimitRepo.restoreAllCurrDailyLimit();
    }
}
