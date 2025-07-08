package dev.aullisia.babelfish.config.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.aullisia.babelfish.BabelFish;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class BabelFishClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config/babelfish", "babelfish-client.json");

    public String preferredLang = "en";

    private static BabelFishClientConfig INSTANCE = new BabelFishClientConfig();

    public static BabelFishClientConfig get() {
        return INSTANCE;
    }

    public static void load() {
        try {
            File file = CONFIG_PATH.toFile();
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    INSTANCE = GSON.fromJson(reader, BabelFishClientConfig.class);
                }
            }
        } catch (Exception e) {
            BabelFish.LOGGER.warn(e.getMessage());
        }
    }

    public static void save() {
        try {
            File file = CONFIG_PATH.toFile();
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (Exception e) {
            BabelFish.LOGGER.warn(e.getMessage());
        }
    }
}