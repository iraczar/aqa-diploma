package ru.netology.page;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selectors.byText;

public class MainPage {

    public CardFormPage openPaymentForm() {
        $$("button").findBy(exactText("Купить")).click();
        $(byText("Оплата по карте")).shouldBe(visible);
        return new CardFormPage();
    }

    public CardFormPage openCreditForm() {
        $$("button").findBy(exactText("Купить в кредит")).click();
        $(byText("Кредит по данным карты")).shouldBe(visible);
        return new CardFormPage();
    }
}