package ru.mealcard.utils.send_models;

import java.nio.file.Path;
import java.util.Map;

public interface Sender {
    public void send(Path file, Map<String, String> metadata);
}
