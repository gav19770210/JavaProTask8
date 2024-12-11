package ru.gav19770210.javapro.task08.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "user_limits", schema = "public", catalog = "javapro_task8")
@Schema(description = "Лимиты пользователя")
public class UserLimitEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ИД лимита пользователя")
    private Long id;

    @NotNull(message = "Поле <userId> должно быть заполнено")
    @Column(name = "user_id", nullable = false)
    @Schema(description = "ИД пользователя")
    private Long userId;

    @Min(value = 0, message = "Значение поля <daily_limit> должно быть больше нуля")
    @Column(name = "daily_limit")
    @Schema(description = "Сумма дневного лимита пользователя")
    private BigDecimal dailyLimit;

    @Column(name = "curr_daily_limit")
    @Schema(description = "Сумма текущего дневного лимита пользователя")
    private BigDecimal currDailyLimit;

    @Column(name = "curr_daily_limit_date")
    @Schema(description = "Дата изменения текущего дневного лимита пользователя")
    private LocalDateTime currDailyLimitDate;
}
