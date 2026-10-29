package ru.netology.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataHelper {

    public static final String APPROVED_NUMBER = "1111 2222 3333 4444";
    public static final String DECLINED_NUMBER = "5555 6666 7777 8888";

    private DataHelper() {
    }

    public static class CardInfo {
        private final String number;
        private final String month;
        private final String year;
        private final String holder;
        private final String cvc;

        public CardInfo(String number, String month, String year, String holder, String cvc) {
            this.number = number;
            this.month = month;
            this.year = year;
            this.holder = holder;
            this.cvc = cvc;
        }

        public String getNumber() {
            return number;
        }

        public String getMonth() {
            return month;
        }

        public String getYear() {
            return year;
        }

        public String getHolder() {
            return holder;
        }

        public String getCvc() {
            return cvc;
        }
    }

    /** Год в формате "yy", смещённый от текущего на указанное число лет. */
    public static String year(int shiftYears) {
        return LocalDate.now().plusYears(shiftYears).format(DateTimeFormatter.ofPattern("yy"));
    }

    /** Валидная карта с указанным номером: месяц 12, год +1, латинский владелец, CVC 123. */
    public static CardInfo validCard(String number) {
        return new CardInfo(number, "12", year(1), "IVAN PETROV", "123");
    }

    public static CardInfo cardWithNumber(String number) {
        return validCard(number);
    }

    public static CardInfo cardWithMonth(String month) {
        return new CardInfo(APPROVED_NUMBER, month, year(1), "IVAN PETROV", "123");
    }

    public static CardInfo cardWithYear(String year) {
        return new CardInfo(APPROVED_NUMBER, "12", year, "IVAN PETROV", "123");
    }
}