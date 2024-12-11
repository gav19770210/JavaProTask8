package ru.gav19770210.javapro.task08.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gav19770210.javapro.task08.entities.UserLimitEntity;

import java.util.Optional;

@Repository
public interface UserLimitRepo extends CrudRepository<UserLimitEntity, Long> {
    Optional<UserLimitEntity> findByUserId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserLimitEntity ule set ule.currDailyLimit = ule.dailyLimit")
    void restoreAllCurrDailyLimit();
}
