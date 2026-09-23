package ru.mealcard.config;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mealcard.controller.ErrorHandler;
import ru.mealcard.controller.GenerateHandler;
import ru.mealcard.controller.MockHandler;
import ru.mealcard.controller.SendHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private static HttpServer server;
    private static ExecutorService executor;

    public static void start() {
        Config config = Config.getInstance();
        executor = Executors.newFixedThreadPool(config.getPoolSize());

        
        try {
            server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);

            server.createContext("/generate", new GenerateHandler(executor));
            server.createContext("/mock", new MockHandler(executor));
            server.createContext("/send", new SendHandler(executor));
            server.createContext("/error", new ErrorHandler(executor));

            server.setExecutor(executor);
            server.start();
        } catch (IOException e) {
            logger.error("failed start server: {}", e.getMessage(), e);
            executor.shutdown();
            throw new IllegalStateException("Server start failed", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(App::stop));
        logger.info("Server started on port {}", config.getPort());
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
        }
        if (executor != null) {
            executor.shutdown();
        }
        logger.info("Server stopped");
    }
}