package ru.gav19770210.javapro.task08.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import ru.gav19770210.javapro.task08.dto.ChangeLimitRequest;
import ru.gav19770210.javapro.task08.dto.SetDailyLimitRequest;
import ru.gav19770210.javapro.task08.entities.UserLimitEntity;
import ru.gav19770210.javapro.task08.services.UserLimitServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgreSQLContainerExtension.class)
@DirtiesContext
public class UserLimitIntegrationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @LocalServerPort
    Integer port;
    private UserLimitEntity userLimitEntityTest;

    @BeforeEach
    void beforeEach(@Autowired Flyway flyway) {
        RestAssured.baseURI = "http://localhost:" + port;

        flyway.clean();
        flyway.migrate();

        userLimitEntityTest = UserLimitEntity.builder()
                .userId(123456L)
                .dailyLimit(UserLimitServiceImpl.DEFAULT_DAILY_LIMIT)
                .currDailyLimit(UserLimitServiceImpl.DEFAULT_DAILY_LIMIT)
                .currDailyLimitDate(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void afterEach(@Autowired Flyway flyway) {
        userLimitEntityTest = null;

        flyway.clean();
    }

    @Test
    @DisplayName("Интеграционный тест")
    public void testTest() throws JsonProcessingException {
        UserLimitEntity userLimitEntityInp;
        UserLimitEntity userLimitEntityOut;

        System.out.println("--> Получение лимита пользователя, которого не существует");
        userLimitEntityInp = userLimitEntityTest;

        RestAssured.given()
                .when()
                .log().all()
                .contentType(ContentType.JSON)
                .pathParam("user_id", userLimitEntityInp.getUserId())
                .get("/limit/{user_id}/get")
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", Matchers.equalTo(String.format(UserLimitServiceImpl.msgLimitNotFoundByUserId,
                        userLimitEntityInp.getUserId())));

        System.out.println("--> Установка дневного лимита пользователя по умолчанию");
        userLimitEntityInp = userLimitEntityTest;

        var setDailyLimitRequest = new SetDailyLimitRequest(userLimitEntityInp.getUserId(), null);

        UserLimitEntity userLimitEntityCreate = RestAssured.given()
                .when()
                .log().all()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(setDailyLimitRequest))
                .post("/limit/set")
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.notNullValue())
                .extract()
                .as(UserLimitEntity.class);
        userLimitEntityOut = userLimitEntityCreate;

        Assertions.assertEquals(userLimitEntityInp.getUserId(), userLimitEntityOut.getUserId());
        Assertions.assertEquals(userLimitEntityInp.getDailyLimit(), userLimitEntityOut.getDailyLimit());
        Assertions.assertEquals(userLimitEntityInp.getCurrDailyLimit(), userLimitEntityOut.getCurrDailyLimit());

        System.out.println("--> Получение лимита пользователя");
        userLimitEntityInp = userLimitEntityCreate;

        UserLimitEntity userLimitEntityGet = RestAssured.given()
                .when()
                .log().all()
                .contentType(ContentType.JSON)
                .pathParam("user_id", userLimitEntityInp.getUserId())
                .get("/limit/{user_id}/get")
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.notNullValue())
                .extract()
                .as(UserLimitEntity.class);
        userLimitEntityOut = userLimitEntityGet;

        Assertions.assertEquals(userLimitEntityInp, userLimitEntityOut);

        System.out.println("--> Уменьшение текущего дневного лимита пользователя");
        userLimitEntityInp = userLimitEntityGet;

        var decAmountcCurrLimit = userLimitEntityInp.getCurrDailyLimit().multiply(BigDecimal.valueOf(0.4));
        var decCurrDailyLimit = userLimitEntityInp.getCurrDailyLimit().subtract(decAmountcCurrLimit);
        var changeLimitRequestDec = new ChangeLimitRequest(userLimitEntityInp.getUserId(), decAmountcCurrLimit);

        UserLimitEntity userLimitEntityCurrDailyLimitDec = RestAssured.given()
                .when()
                .log().all()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(changeLimitRequestDec))
                .post("/limit/decrease")
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.notNullValue())
                .extract()
                .as(UserLimitEntity.class);
        userLimitEntityOut = userLimitEntityCurrDailyLimitDec;

        Assertions.assertEquals(userLimitEntityInp.getId(), userLimitEntityOut.getId());
        Assertions.assertEquals(userLimitEntityInp.getUserId(), userLimitEntityOut.getUserId());
        Assertions.assertEquals(userLimitEntityInp.getDailyLimit(), userLimitEntityOut.getDailyLimit());
        Assertions.assertEquals(decCurrDailyLimit, userLimitEntityOut.getCurrDailyLimit());

        System.out.println("--> Уменьшение текущего дневного лимита пользователя на сумму больше остатка лимита");
        userLimitEntityInp = userLimitEntityCurrDailyLimitDec;

        decAmountcCurrLimit = userLimitEntityInp.getCurrDailyLimit().multiply(BigDecimal.valueOf(1.2));
        changeLimitRequestDec = new ChangeLimitRequest(userLimitEntityInp.getUserId(), decAmountcCurrLimit);

        RestAssured.given()
                .when()
                .log().all()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(changeLimitRequestDec))
                .post("/limit/decrease")
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", Matchers.equalTo(String.format(UserLimitServiceImpl.msgDailyLimitNotEnough, decAmountcCurrLimit)));

        System.out.println("--> Увеличение текущего дневного лимита пользователя");
        userLimitEntityInp = userLimitEntityCurrDailyLimitDec;

        var incAmountcCurrLimit = userLimitEntityInp.getCurrDailyLimit().multiply(BigDecimal.valueOf(0.2));
        var incCurrDailyLimit = userLimitEntityInp.getCurrDailyLimit().add(incAmountcCurrLimit);
        var changeLimitRequestInc = new ChangeLimitRequest(userLimitEntityInp.getUserId(), incAmountcCurrLimit);

        UserLimitEntity userLimitEntityCurrDailyLimitInc = RestAssured.given()
                .when()
                .log().all()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(changeLimitRequestInc))
                .post("/limit/increase")
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.notNullValue())
                .extract()
                .as(UserLimitEntity.class);
        userLimitEntityOut = userLimitEntityCurrDailyLimitInc;

        Assertions.assertEquals(userLimitEntityInp.getId(), userLimitEntityOut.getId());
        Assertions.assertEquals(userLimitEntityInp.getUserId(), userLimitEntityOut.getUserId());
        Assertions.assertEquals(userLimitEntityInp.getDailyLimit(), userLimitEntityOut.getDailyLimit());
        Assertions.assertEquals(incCurrDailyLimit, userLimitEntityOut.getCurrDailyLimit());

        System.out.println("--> Уменьшение дневного лимита пользователя");
        userLimitEntityInp = userLimitEntityCurrDailyLimitInc;

        var decAmountLimit = UserLimitServiceImpl.DEFAULT_DAILY_LIMIT.multiply(BigDecimal.valueOf(0.5));
        var decDailyLimit = userLimitEntityInp.getDailyLimit().subtract(decAmountLimit);
        decCurrDailyLimit = userLimitEntityInp.getCurrDailyLimit().subtract(decAmountLimit);
        var setDailyLimitRequestDec = new SetDailyLimitRequest(userLimitEntityInp.getUserId(), decDailyLimit);

        UserLimitEntity userLimitEntityDailyLimitDec = RestAssured.given()
                .when()
                .log().all()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(setDailyLimitRequestDec))
                .post("/limit/set")
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.notNullValue())
                .extract()
                .as(UserLimitEntity.class);
        userLimitEntityOut = userLimitEntityDailyLimitDec;

        Assertions.assertEquals(userLimitEntityInp.getId(), userLimitEntityOut.getId());
        Assertions.assertEquals(userLimitEntityInp.getUserId(), userLimitEntityOut.getUserId());
        Assertions.assertEquals(decDailyLimit, userLimitEntityOut.getDailyLimit());
        Assertions.assertEquals(decCurrDailyLimit, userLimitEntityOut.getCurrDailyLimit());

        System.out.println("--> Увеличение дневного лимита пользователя");
        userLimitEntityInp = userLimitEntityDailyLimitDec;

        var incAmountLimit = UserLimitServiceImpl.DEFAULT_DAILY_LIMIT;
        var incDailyLimit = userLimitEntityInp.getDailyLimit().add(incAmountLimit);
        incCurrDailyLimit = userLimitEntityInp.getCurrDailyLimit().add(incAmountLimit);
        var setDailyLimitRequestInc = new SetDailyLimitRequest(userLimitEntityInp.getUserId(), incDailyLimit);

        UserLimitEntity userLimitEntityDailyLimitInc = RestAssured.given()
                .when()
                .log().all()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(setDailyLimitRequestInc))
                .post("/limit/set")
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.notNullValue())
                .extract()
                .as(UserLimitEntity.class);
        userLimitEntityOut = userLimitEntityDailyLimitInc;

        Assertions.assertEquals(userLimitEntityInp.getId(), userLimitEntityOut.getId());
        Assertions.assertEquals(userLimitEntityInp.getUserId(), userLimitEntityOut.getUserId());
        Assertions.assertEquals(incDailyLimit, userLimitEntityOut.getDailyLimit());
        Assertions.assertEquals(incCurrDailyLimit, userLimitEntityOut.getCurrDailyLimit());
    }
}
