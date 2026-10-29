import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GateSimulator {

    private static final Pattern NUMBER = Pattern.compile("\"number\"\\s*:\\s*\"([^\"]*)\"");

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(9999), 0);
        server.createContext("/payment", GateSimulator::handle);
        server.createContext("/credit", GateSimulator::handle);
        server.start();
        System.out.println("Local gate simulator started on http://localhost:9999 (/payment, /credit)");
    }

    private static void handle(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Matcher m = NUMBER.matcher(body);
        String number = m.find() ? m.group(1).replace(" ", "") : "";
        int code;
        String json;
        if (number.equals("1111222233334444")) {
            code = 200;
            json = "{\"id\":\"" + UUID.randomUUID() + "\",\"status\":\"APPROVED\"}";
        } else if (number.equals("5555666677778888")) {
            code = 200;
            json = "{\"id\":\"" + UUID.randomUUID() + "\",\"status\":\"DECLINED\"}";
        } else {
            code = 400;
            json = "{\"error\":\"unknown card\"}";
        }
        System.out.println(ex.getRequestURI() + " number=" + number + " -> " + code + " " + json);
        byte[] out = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(code, out.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(out);
        }
    }
}
