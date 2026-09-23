package ru.mealcard.test;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Receiver {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8090), 0);

        server.createContext("/upload", exchange -> {
            byte[] body = exchange.getRequestBody().readAllBytes();
            System.out.println("Received " + body.length + " bytes, " +
                    exchange.getRequestMethod() + " ===");
            exchange.getRequestHeaders().forEach((k, v) ->
                    System.out.println(k + ": " + v));

            byte[] resp = "{\"status\":\"ok\"}".getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
            exchange.close();
        });

        server.start();
        System.out.println("Test upload server on http://localhost:8090/upload");
    }
}