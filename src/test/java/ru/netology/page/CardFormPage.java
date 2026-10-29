package ru.netology.page;

import com.codeborne.selenide.ElementsCollection;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CardFormPage {

    private static final Duration BANK_TIMEOUT = Duration.ofSeconds(20);

    // Порядок полей на форме: номер, месяц, год, владелец, CVC
    private ElementsCollection fields() {
        return $$(".input__control");
    }

    public void fill(DataHelper.CardInfo card) {
        fields().get(0).setValue(card.getNumber());
        fields().get(1).setValue(card.getMonth());
        fields().get(2).setValue(card.getYear());
        fields().get(3).setValue(card.getHolder());
        fields().get(4).setValue(card.getCvc());
    }

    public void submit() {
        $$("button").findBy(com.codeborne.selenide.Condition.text("Продолжить")).click();
    }

    public void shouldSeeApproved() {
        $(withText("Операция одобрена Банком")).shouldBe(visible, BANK_TIMEOUT);
    }

    public void shouldSeeDeclined() {
        $(withText("Банк отказал в проведении операции")).shouldBe(visible, BANK_TIMEOUT);
    }

    public void shouldSeeFieldError(String text) {
        $(withText(text)).shouldBe(visible);
    }
}