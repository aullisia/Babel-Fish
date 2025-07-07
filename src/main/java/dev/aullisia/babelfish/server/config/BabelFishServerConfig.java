package dev.aullisia.babelfish.server.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.aullisia.babelfish.BabelFish;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class BabelFishServerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config", "babelfish-server.json");

    public ModOptions modoptions = new ModOptions();

    public static class ModOptions {
        public String _comment = "The default language code for players that have not configured it (example: en)";
        public String defaultLanguage = "en";
    }

    private static BabelFishServerConfig INSTANCE = new BabelFishServerConfig();

    public static BabelFishServerConfig get() {
        return INSTANCE;
    }

    public static void load() {
        try {
            File file = CONFIG_PATH.toFile();
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    INSTANCE = GSON.fromJson(reader, BabelFishServerConfig.class);
                }
            } else {
                save();
            }
        } catch (Exception e) {
            BabelFish.LOGGER.warn(e.getMessage());
            INSTANCE = new BabelFishServerConfig();
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


