package ru.gav19770210.javapro.task08.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Запрос установки дневного лимита пользователя")
public class SetDailyLimitRequest {
    @NotNull(message = "Поле <userId> должно быть заполнено")
    @Schema(description = "ИД пользователя")
    private Long userId;

    @Positive(message = "Значение поля <daily_limit> должно быть больше нуля")
    @Schema(description = "Сумма дневного лимита пользователя")
    private BigDecimal dailyLimit;
}
