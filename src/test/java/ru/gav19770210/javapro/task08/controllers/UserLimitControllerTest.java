package ru.gav19770210.javapro.task08.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.gav19770210.javapro.task08.dto.ChangeLimitRequest;
import ru.gav19770210.javapro.task08.dto.SetDailyLimitRequest;
import ru.gav19770210.javapro.task08.entities.UserLimitEntity;
import ru.gav19770210.javapro.task08.services.UserLimitService;
import ru.gav19770210.javapro.task08.services.UserLimitServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserLimitController.class)
public class UserLimitControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserLimitService userLimitService;
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
    public void getLimitTest() throws Exception {

        Mockito.when(userLimitService.getLimit(userLimitEntityTest.getUserId())).thenReturn(userLimitEntityTest);

        mockMvc.perform(get("/limit/{user_id}/get", userLimitEntityTest.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(userLimitEntityTest.getId()))
                .andExpect(jsonPath("userId").value(userLimitEntityTest.getUserId()))
                .andExpect(jsonPath("dailyLimit").value(userLimitEntityTest.getDailyLimit().doubleValue()))
                .andExpect(jsonPath("currDailyLimit").value(userLimitEntityTest.getCurrDailyLimit().doubleValue()));

        Mockito.verify(userLimitService).getLimit(userLimitEntityTest.getUserId());
    }

    @Test
    @DisplayName("getLimit. Проверка на отсутствие лимита пользователя")
    public void getLimitTest_NotExistByUserId() throws Exception {
        var errorMessage = String.format(UserLimitServiceImpl.msgLimitNotFoundByUserId, userLimitEntityTest.getUserId());

        Mockito.doThrow(new NoSuchElementException(errorMessage))
                .when(userLimitService).getLimit(ArgumentMatchers.any());

        mockMvc.perform(get("/limit/{user_id}/get", userLimitEntityTest.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value(errorMessage));

        Mockito.verify(userLimitService).getLimit(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("setDailyLimit. Уменьшение дневного лимита пользователя")
    public void setDailyLimitTest_Dec() throws Exception {

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

        var setDailyLimitRequest = new SetDailyLimitRequest(userLimitEntityTest.getUserId(), newDailyLimit);

        Mockito.when(userLimitService.setDailyLimit(userLimitEntityTest.getUserId(), newDailyLimit))
                .thenReturn(userLimitEntitySave);

        mockMvc.perform(post("/limit/set")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(setDailyLimitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(userLimitEntitySave.getId()))
                .andExpect(jsonPath("userId").value(userLimitEntitySave.getUserId()))
                .andExpect(jsonPath("dailyLimit").value(userLimitEntitySave.getDailyLimit().doubleValue()))
                .andExpect(jsonPath("currDailyLimit").value(userLimitEntitySave.getCurrDailyLimit().doubleValue()));

        Mockito.verify(userLimitService).setDailyLimit(userLimitEntityTest.getUserId(), newDailyLimit);
    }

    @Test
    @DisplayName("setDailyLimit. Увеличение дневного лимита пользователя")
    public void setDailyLimitTest_Inc() throws Exception {

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

        var setDailyLimitRequest = new SetDailyLimitRequest(userLimitEntityTest.getUserId(), newDailyLimit);

        Mockito.when(userLimitService.setDailyLimit(userLimitEntityTest.getUserId(), newDailyLimit))
                .thenReturn(userLimitEntitySave);

        mockMvc.perform(post("/limit/set")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(setDailyLimitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(userLimitEntitySave.getId()))
                .andExpect(jsonPath("userId").value(userLimitEntitySave.getUserId()))
                .andExpect(jsonPath("dailyLimit").value(userLimitEntitySave.getDailyLimit().doubleValue()))
                .andExpect(jsonPath("currDailyLimit").value(userLimitEntitySave.getCurrDailyLimit().doubleValue()));

        Mockito.verify(userLimitService).setDailyLimit(userLimitEntityTest.getUserId(), newDailyLimit);
    }

    @Test
    @DisplayName("decreaseLimit. Уменьшение текущего дневного лимита пользователя")
    public void decreaseLimitTest() throws Exception {

        var decAmountLimit = userLimitEntityTest.getCurrDailyLimit().multiply(BigDecimal.valueOf(0.2));
        var newCurrDailyLimit = userLimitEntityTest.getCurrDailyLimit().subtract(decAmountLimit);

        var userLimitEntitySave = UserLimitEntity.builder()
                .id(userLimitEntityTest.getId())
                .userId(userLimitEntityTest.getUserId())
                .dailyLimit(userLimitEntityTest.getDailyLimit())
                .currDailyLimit(newCurrDailyLimit)
                .currDailyLimitDate(userLimitEntityTest.getCurrDailyLimitDate())
                .build();

        var changeLimitRequest = new ChangeLimitRequest(userLimitEntityTest.getUserId(), decAmountLimit);

        Mockito.when(userLimitService.decreaseLimit(userLimitEntityTest.getUserId(), decAmountLimit))
                .thenReturn(userLimitEntitySave);

        mockMvc.perform(post("/limit/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeLimitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(userLimitEntitySave.getId()))
                .andExpect(jsonPath("userId").value(userLimitEntitySave.getUserId()))
                .andExpect(jsonPath("dailyLimit").value(userLimitEntitySave.getDailyLimit().doubleValue()))
                .andExpect(jsonPath("currDailyLimit").value(userLimitEntitySave.getCurrDailyLimit().doubleValue()));

        Mockito.verify(userLimitService).decreaseLimit(userLimitEntityTest.getUserId(), decAmountLimit);
    }

    @Test
    @DisplayName("decreaseLimit. Уменьшение текущего дневного лимита пользователя на сумму больше остатка лимита")
    public void decreaseLimitTest_NotEnough() throws Exception {
        var decAmountLimit = userLimitEntityTest.getCurrDailyLimit().multiply(BigDecimal.valueOf(1.2));
        var errorMessage = String.format(UserLimitServiceImpl.msgDailyLimitNotEnough, decAmountLimit);
        var changeLimitRequest = new ChangeLimitRequest(userLimitEntityTest.getUserId(), decAmountLimit);

        Mockito.doThrow(new NoSuchElementException(errorMessage))
                .when(userLimitService).decreaseLimit(userLimitEntityTest.getUserId(), decAmountLimit);

        mockMvc.perform(post("/limit/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeLimitRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value(errorMessage));

        Mockito.verify(userLimitService).decreaseLimit(userLimitEntityTest.getUserId(), decAmountLimit);
    }

    @Test
    @DisplayName("increaseLimit. Увеличение текущего дневного лимита пользователя")
    public void increaseLimitTest() throws Exception {

        var incAmountLimit = userLimitEntityTest.getCurrDailyLimit().multiply(BigDecimal.valueOf(0.2));
        var newCurrDailyLimit = userLimitEntityTest.getCurrDailyLimit().add(incAmountLimit);

        var userLimitEntitySave = UserLimitEntity.builder()
                .id(userLimitEntityTest.getId())
                .userId(userLimitEntityTest.getUserId())
                .dailyLimit(userLimitEntityTest.getDailyLimit())
                .currDailyLimit(newCurrDailyLimit)
                .currDailyLimitDate(userLimitEntityTest.getCurrDailyLimitDate())
                .build();

        var changeLimitRequest = new ChangeLimitRequest(userLimitEntityTest.getUserId(), incAmountLimit);

        Mockito.when(userLimitService.increaseLimit(userLimitEntityTest.getUserId(), incAmountLimit))
                .thenReturn(userLimitEntitySave);

        mockMvc.perform(post("/limit/increase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeLimitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(userLimitEntitySave.getId()))
                .andExpect(jsonPath("userId").value(userLimitEntitySave.getUserId()))
                .andExpect(jsonPath("dailyLimit").value(userLimitEntitySave.getDailyLimit().doubleValue()))
                .andExpect(jsonPath("currDailyLimit").value(userLimitEntitySave.getCurrDailyLimit().doubleValue()));

        Mockito.verify(userLimitService).increaseLimit(userLimitEntityTest.getUserId(), incAmountLimit);
    }

    @Test
    @DisplayName("increaseLimit. Увеличение текущего дневного лимита пользователя выше номинала")
    public void increaseLimitTest_MoreThenNominal() throws Exception {

        var incAmountLimit = userLimitEntityTest.getCurrDailyLimit().multiply(BigDecimal.valueOf(1.2));
        var newCurrDailyLimit = userLimitEntityTest.getDailyLimit();

        var userLimitEntitySave = UserLimitEntity.builder()
                .id(userLimitEntityTest.getId())
                .userId(userLimitEntityTest.getUserId())
                .dailyLimit(userLimitEntityTest.getDailyLimit())
                .currDailyLimit(newCurrDailyLimit)
                .currDailyLimitDate(userLimitEntityTest.getCurrDailyLimitDate())
                .build();

        var changeLimitRequest = new ChangeLimitRequest(userLimitEntityTest.getUserId(), incAmountLimit);

        Mockito.when(userLimitService.increaseLimit(userLimitEntityTest.getUserId(), incAmountLimit))
                .thenReturn(userLimitEntitySave);

        mockMvc.perform(post("/limit/increase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeLimitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(userLimitEntitySave.getId()))
                .andExpect(jsonPath("userId").value(userLimitEntitySave.getUserId()))
                .andExpect(jsonPath("dailyLimit").value(userLimitEntitySave.getDailyLimit().doubleValue()))
                .andExpect(jsonPath("currDailyLimit").value(userLimitEntitySave.getCurrDailyLimit().doubleValue()));

        Mockito.verify(userLimitService).increaseLimit(userLimitEntityTest.getUserId(), incAmountLimit);
    }
}
