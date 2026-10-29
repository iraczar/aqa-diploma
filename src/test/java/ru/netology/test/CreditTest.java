package ru.netology.test;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SqlHelper;
import ru.netology.page.CardFormPage;
import ru.netology.page.MainPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Feature("Кредит по данным карты")
class CreditTest extends BaseTest {

    private CardFormPage openForm() {
        return new MainPage().openCreditForm();
    }

    @Test
    @DisplayName("Кредит: карта APPROVED — успех и запись APPROVED в БД")
    void shouldGetCreditWithApprovedCard() {
        CardFormPage form = openForm();
        form.fill(DataHelper.validCard(DataHelper.APPROVED_NUMBER));
        form.submit();
        form.shouldSeeApproved();
        assertEquals("APPROVED", SqlHelper.lastCreditStatus());
        assertEquals(0, SqlHelper.countPayments());
    }

    @Test
    @DisplayName("Кредит: карта DECLINED — отказ и запись DECLINED в БД")
    void shouldDeclineCreditWithDeclinedCard() {
        CardFormPage form = openForm();
        form.fill(DataHelper.validCard(DataHelper.DECLINED_NUMBER));
        form.submit();
        assertEquals("DECLINED", SqlHelper.lastCreditStatus());
        form.shouldSeeDeclined();
    }

    @Test
    @DisplayName("Кредит: пустая форма — ошибки обязательных полей, в БД ничего нет")
    void shouldShowRequiredErrorsOnEmptyForm() {
        CardFormPage form = openForm();
        form.submit();
        form.shouldSeeFieldError("Поле обязательно для заполнения");
        assertEquals(0, SqlHelper.countCredits());
    }

    @Test
    @DisplayName("Кредит: короткий номер карты — «Неверный формат»")
    void shouldRejectShortCardNumber() {
        CardFormPage form = openForm();
        form.fill(DataHelper.cardWithNumber("1111 2222 3333 444"));
        form.submit();
        form.shouldSeeFieldError("Неверный формат");
        assertEquals(0, SqlHelper.countCredits());
    }

    @Test
    @DisplayName("Кредит: истёкший год — «Истёк срок действия карты»")
    void shouldRejectExpiredYear() {
        CardFormPage form = openForm();
        form.fill(DataHelper.cardWithYear(DataHelper.year(-1)));
        form.submit();
        form.shouldSeeFieldError("Истёк срок действия карты");
        assertEquals(0, SqlHelper.countCredits());
    }

    @Test
    @DisplayName("Кредит: месяц 13 — «Неверно указан срок действия карты»")
    void shouldRejectMonthThirteen() {
        CardFormPage form = openForm();
        form.fill(DataHelper.cardWithMonth("13"));
        form.submit();
        form.shouldSeeFieldError("Неверно указан срок действия карты");
        assertEquals(0, SqlHelper.countCredits());
    }
}
