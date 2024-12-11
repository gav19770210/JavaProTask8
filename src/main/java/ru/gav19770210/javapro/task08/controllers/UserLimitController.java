package ru.gav19770210.javapro.task08.controllers;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.api.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.gav19770210.javapro.task08.dto.ChangeLimitRequest;
import ru.gav19770210.javapro.task08.dto.SetDailyLimitRequest;
import ru.gav19770210.javapro.task08.entities.UserLimitEntity;
import ru.gav19770210.javapro.task08.services.UserLimitService;

@OpenAPIDefinition(
        info = @Info(
                title = "User Limit Controller",
                version = "1.0",
                description = "My API Services"
        ),
        servers = {
                @Server(
                        description = "test server",
                        url = "http://localhost:8080"
                )
        }
)
@RestController
@RequestMapping(value = "limit", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserLimitController {
    private final UserLimitService userLimitService;

    @Autowired
    public UserLimitController(UserLimitService userLimitService) {
        this.userLimitService = userLimitService;
    }

    @Operation(summary = "Получение лимита пользователя",
            responses = {
                    @ApiResponse(description = "Данные лимита пользователя",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserLimitEntity.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    )
            }
    )
    @GetMapping(value = "/{user_id}/get")
    public ResponseEntity<UserLimitEntity> getLimit(@PathVariable("user_id") Long userId) {
        var userLimitGet = userLimitService.getLimit(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userLimitGet);
    }

    @Operation(summary = "Установка дневного лимита пользователя",
            responses = {
                    @ApiResponse(description = "Данные лимита пользователя",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserLimitEntity.class)
                            )
                    )
            }
    )
    @PostMapping(value = "/set", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserLimitEntity> setDailyLimit(@RequestBody @Validated SetDailyLimitRequest setDailyLimitRequest) {
        var userLimitEntity = userLimitService.setDailyLimit(setDailyLimitRequest.getUserId(),
                setDailyLimitRequest.getDailyLimit());

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userLimitEntity);
    }

    @Operation(summary = "Уменьшение текущего дневного лимита пользователя",
            responses = {
                    @ApiResponse(description = "Данные лимита пользователя",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserLimitEntity.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    )
            }
    )
    @PostMapping(value = "/decrease", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserLimitEntity> decreaseLimit(@RequestBody @Validated ChangeLimitRequest changeLimitRequest) {
        var userLimitEntity = userLimitService.decreaseLimit(changeLimitRequest.getUserId(),
                changeLimitRequest.getAmount());

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userLimitEntity);
    }

    @Operation(summary = "Увеличение текущего дневного лимита пользователя",
            responses = {
                    @ApiResponse(description = "Данные лимита пользователя",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserLimitEntity.class)
                            )
                    )
            }
    )
    @PostMapping(value = "/increase", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserLimitEntity> increaseLimit(@RequestBody @Validated ChangeLimitRequest changeLimitRequest) {
        var userLimitEntity = userLimitService.increaseLimit(changeLimitRequest.getUserId(),
                changeLimitRequest.getAmount());

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userLimitEntity);
    }
}
