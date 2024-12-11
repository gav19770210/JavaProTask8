package ru.gav19770210.javapro.task08.services;

import ru.gav19770210.javapro.task08.entities.UserLimitEntity;

import java.math.BigDecimal;

public interface UserLimitService {

    UserLimitEntity getLimit(Long userId);

    UserLimitEntity setDailyLimit(Long userId, BigDecimal dailyLimit);

    UserLimitEntity decreaseLimit(Long userId, BigDecimal amount);

    UserLimitEntity increaseLimit(Long userId, BigDecimal amount);

    void restoreAllCurrDailyLimit();
}
