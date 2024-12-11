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
@Schema(description = "Запрос изменения текущего лимита пользователя")
public class ChangeLimitRequest {
    @NotNull(message = "Поле <userId> должно быть заполнено")
    @Schema(description = "ИД пользователя")
    private Long userId;

    @NotNull(message = "Поле <amount> должно быть заполнено")
    @Positive(message = "Значение поля <amount> должно быть больше нуля")
    @Schema(description = "Сумма изменения текущего лимита пользователя")
    private BigDecimal amount;
}
