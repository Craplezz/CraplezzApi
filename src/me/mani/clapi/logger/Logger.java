package me.mani.clapi.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Schuckmann on 18.05.2016.
 */
public class Logger {

    private static List<Consumer<String>> loggers = new ArrayList<>();

    public static void registerLogger(Consumer<String> logger) {
        loggers.add(logger);
    }

    public static void log(String... messages) {
        for (Consumer<String> consumer : loggers)
            for (String message : messages)
                consumer.accept(message);
    }

}
