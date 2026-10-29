package ru.netology.data;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlHelper {

    private static final QueryRunner RUNNER = new QueryRunner();

    private SqlHelper() {
    }

    private static Connection connection() throws SQLException {
        return DriverManager.getConnection(
                System.getProperty("db.url", "jdbc:mysql://localhost:3306/app"),
                System.getProperty("db.user", "app"),
                System.getProperty("db.password", "pass"));
    }

    public static void cleanDatabase() {
        try (Connection conn = connection()) {
            RUNNER.update(conn, "DELETE FROM order_entity");
            RUNNER.update(conn, "DELETE FROM payment_entity");
            RUNNER.update(conn, "DELETE FROM credit_request_entity");
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось очистить БД: " + e.getMessage(), e);
        }
    }

    /** Статус последнего платежа. Ждёт появления записи до 10 секунд, иначе вернёт null. */
    public static String lastPaymentStatus() {
        return waitForString("SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1");
    }

    /** Статус последней кредитной заявки. Ждёт появления записи до 10 секунд, иначе вернёт null. */
    public static String lastCreditStatus() {
        return waitForString("SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1");
    }

    public static long countPayments() {
        return count("payment_entity");
    }

    public static long countCredits() {
        return count("credit_request_entity");
    }

    private static long count(String table) {
        try (Connection conn = connection()) {
            Number n = RUNNER.query(conn, "SELECT COUNT(*) FROM " + table, new ScalarHandler<Number>());
            return n.longValue();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка запроса к БД: " + e.getMessage(), e);
        }
    }

    private static String waitForString(String sql) {
        try (Connection conn = connection()) {
            for (int i = 0; i < 20; i++) {
                String value = RUNNER.query(conn, sql, new ScalarHandler<String>());
                if (value != null) {
                    return value;
                }
                Thread.sleep(500);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка запроса к БД: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}