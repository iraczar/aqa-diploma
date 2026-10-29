# Автоматизация тестирования сервиса покупки тура

UI-тесты (Selenide, JUnit 5) обеих форм покупки («Купить» и «Купить в кредит»), проверка записей в БД (MySQL / PostgreSQL), отчёты Gradle и Allure.

## Что нужно установить заранее

- JDK 17 или новее
- Google Chrome
- Docker Desktop (должен быть запущен)
- Git

## Запуск

Все команды выполняются в терминале из корня проекта. Для запуска нужны три отдельных окна терминала: приложение работает, пока оно открыто.

1. Клонировать проект:
   ```
   git clone https://github.com/iraczar/aqa-diploma.git
   cd aqa-diploma
   ```
2. Поднять базы данных (подождать около 30 секунд):
   ```
   docker compose up -d
   ```
3. Запустить приложение в отдельном терминале.

   С MySQL:
   ```
   java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" "-Dspring.datasource.username=app" "-Dspring.datasource.password=pass" -jar artifacts/aqa-shop.jar
   ```
   С PostgreSQL:
   ```
   java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" "-Dspring.datasource.username=app" "-Dspring.datasource.password=pass" -jar artifacts/aqa-shop.jar
   ```
   Приложение готово, когда в логе появилась строка `Started ShopApplication`, а страница http://localhost:8080 открывается.

   Банковский симулятор (185.119.57.197:9999) уже указан в файле `application.properties` в корне проекта, поэтому приложение нужно запускать именно из корня проекта.
4. Запустить тесты в другом терминале.

   Windows:
   ```
   .\gradlew.bat clean test
   ```
   Mac / Linux:
   ```
   ./gradlew clean test
   ```
   Если приложение запущено с PostgreSQL, добавьте параметр:
   ```
   .\gradlew.bat clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app"
   ```
   Чтобы браузер не открывался, добавьте `-Dheadless=true`.

   Параметры тестов: `db.url` (по умолчанию `jdbc:mysql://localhost:3306/app`), `db.user` (`app`), `db.password` (`pass`), `sut.url` (`http://localhost:8080`), `headless` (`false`).

## Если симулятор банка недоступен из вашей сети

Для отладки есть локальная заглушка (не заменяет настоящий симулятор для итоговой проверки).

1. В отдельном терминале:
   ```
   java tools/GateSimulator.java
   ```
2. Приложение запустить с переопределёнными адресами банка:
   ```
   java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" "-Dspring.datasource.username=app" "-Dspring.datasource.password=pass" "-Dspring.payment-gate.url=http://localhost:9999/payment" "-Dspring.credit-gate.url=http://localhost:9999/credit" -jar artifacts/aqa-shop.jar
   ```

## Отчёты

Gradle-отчёт: открыть файл `build/reports/tests/test/index.html` в браузере.

Allure-отчёт (после прогона тестов):
```
.\gradlew.bat allureServe
```
Статический отчёт: `.\gradlew.bat allureReport`.

## Остановка

- Приложение и заглушка: Ctrl+C в их терминалах.
- Базы данных: `docker compose down`.

## Документы

`Plan.md` (план автоматизации), `Report.md` (отчёт о тестировании), `Summary.md` (итоги автоматизации), найденные баги: вкладка Issues репозитория.