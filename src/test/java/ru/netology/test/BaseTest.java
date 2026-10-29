package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.netology.data.SqlHelper;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

public abstract class BaseTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(false));
        Configuration.baseUrl = System.getProperty("sut.url", "http://localhost:8080");
        Configuration.headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        Configuration.browserSize = "1280x1024";
        Configuration.timeout = 8000;
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("AllureSelenide");
    }

    @BeforeEach
    void setUp() {
        SqlHelper.cleanDatabase();
        open("/");
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }
}