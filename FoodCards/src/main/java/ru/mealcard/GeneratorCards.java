package ru.mealcard;

import ru.mealcard.config.App;


public class GeneratorCards {
    public static void main(String[] args) {
        long pid = ProcessHandle.current().pid();
        System.out.println("pid: " + pid);
        App.start();
    }
}