package ru.gav19770210.javapro.task08;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
Реализовать REST микросервис лимитов, который имеет следующий функционал:
- Для каждого юзера в БД хранится дневной лимит возможных платежей
  (первоначально 10000.00. Считаем, что раз в несколько месяцев он может меняться)
- В 00.00 каждого дня лимит для всех пользователей должен быть сброшен
- При успешном проведении платежа лимит должен быть уменьшен на соответствующую сумму
- Если вдруг платеж по какой-то причине не прошел, необходимо иметь возможность восстановить списанный лимит
  (тут сами выбираете стратегию уменьшения/восстановления лимитов)
- Поскольку сервиса клиентов у нас нет, в БД храним лимиты для «клиентов» с ID 1-100
- Поскольку считаем, что gateway не пропустит в систему несуществующего клиента, то при запросе лимита с ID,
  который отсутствует в БД, создаем новую запись под него со стандартным значением лимита
 */
@SpringBootApplication(scanBasePackages = "ru.gav19770210.javapro.task08")
//@EnableScheduling
public class UserLimitApp {
    public static void main(String[] args) {
        SpringApplication.run(UserLimitApp.class, args);
    }
}
