package ru.netology.test;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SqlHelper;
import ru.netology.page.CardFormPage;
import ru.netology.page.MainPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Feature("Оплата по карте")
class PaymentTest extends BaseTest {

    private CardFormPage openForm() {
        return new MainPage().openPaymentForm();
    }

    @Test
    @DisplayName("Оплата: карта APPROVED — успех и запись APPROVED в БД")
    void shouldPayWithApprovedCard() {
        CardFormPage form = openForm();
        form.fill(DataHelper.validCard(DataHelper.APPROVED_NUMBER));
        form.submit();
        form.shouldSeeApproved();
        assertEquals("APPROVED", SqlHelper.lastPaymentStatus());
        assertEquals(0, SqlHelper.countCredits());
    }

    @Test
    @DisplayName("Оплата: карта DECLINED — отказ и запись DECLINED в БД")
    void shouldDeclinePaymentWithDeclinedCard() {
        CardFormPage form = openForm();
        form.fill(DataHelper.validCard(DataHelper.DECLINED_NUMBER));
        form.submit();
        assertEquals("DECLINED", SqlHelper.lastPaymentStatus());
        form.shouldSeeDeclined();
    }

    @Test
    @DisplayName("Оплата: пустая форма — ошибки обязательных полей, в БД ничего нет")
    void shouldShowRequiredErrorsOnEmptyForm() {
        CardFormPage form = openForm();
        form.submit();
        form.shouldSeeFieldError("Поле обязательно для заполнения");
        assertEquals(0, SqlHelper.countPayments());
    }

    @Test
    @DisplayName("Оплата: короткий номер карты — «Неверный формат»")
    void shouldRejectShortCardNumber() {
        CardFormPage form = openForm();
        form.fill(DataHelper.cardWithNumber("1111 2222 3333 444"));
        form.submit();
        form.shouldSeeFieldError("Неверный формат");
        assertEquals(0, SqlHelper.countPayments());
    }

    @Test
    @DisplayName("Оплата: истёкший год — «Истёк срок действия карты»")
    void shouldRejectExpiredYear() {
        CardFormPage form = openForm();
        form.fill(DataHelper.cardWithYear(DataHelper.year(-1)));
        form.submit();
        form.shouldSeeFieldError("Истёк срок действия карты");
        assertEquals(0, SqlHelper.countPayments());
    }

    @Test
    @DisplayName("Оплата: месяц 13 — «Неверно указан срок действия карты»")
    void shouldRejectMonthThirteen() {
        CardFormPage form = openForm();
        form.fill(DataHelper.cardWithMonth("13"));
        form.submit();
        form.shouldSeeFieldError("Неверно указан срок действия карты");
        assertEquals(0, SqlHelper.countPayments());
    }
}