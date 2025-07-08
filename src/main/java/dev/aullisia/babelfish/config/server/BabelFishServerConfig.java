package dev.aullisia.babelfish.config.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.aullisia.babelfish.BabelFish;
import dev.aullisia.babelfish.TranslateService;
import dev.aullisia.babelfish.translators.CustomTranslator;
import dev.aullisia.babelfish.translators.TranslatorConfig;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static dev.aullisia.babelfish.config.server.ServerTranslatorConfigUtils.createExampleTranslatorConfig;

public class BabelFishServerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config/babelfish", "babelfish-server.yaml");
    public static final Path TRANSLATORS_DIR = CONFIG_PATH.getParent().resolve("translators");

    private final List<TranslatorConfig> loadedTranslatorConfigs = new ArrayList<>();
    private final Map<String, CustomTranslator> customTranslators = new ConcurrentHashMap<>();

    public String defaultLanguage = "en";
    public String selectedTranslator = "LibreTranslate";

    private static BabelFishServerConfig INSTANCE = new BabelFishServerConfig();

    public CustomTranslator getTranslator(String id) {
        return customTranslators.get(id);
    }

    public static BabelFishServerConfig getInstance() {
        return INSTANCE;
    }

    public static void load() {
        File file = CONFIG_PATH.toFile();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                LoaderOptions options = new LoaderOptions();
                Yaml yaml = new Yaml(new Constructor(BabelFishServerConfig.class, options));
                INSTANCE = yaml.load(reader);
                if (INSTANCE == null) INSTANCE = new BabelFishServerConfig();
            } catch (Exception e) {
                BabelFish.LOGGER.warn("Failed to load babelfish-server.yaml: " + e.getMessage());
                INSTANCE = new BabelFishServerConfig();
            }
        } else {
            save();
        }

        INSTANCE.loadTranslatorConfigs();
    }

    public static void save() {
        try {
            File file = CONFIG_PATH.toFile();
            file.getParentFile().mkdirs();

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            options.setIndent(2);

            ServerTranslatorConfigUtils.NoTagRepresenter representer = new ServerTranslatorConfigUtils.NoTagRepresenter();

            Yaml yaml = new Yaml(representer, options);

            try (FileWriter writer = new FileWriter(file)) {
                yaml.dump(INSTANCE, writer);
            }
        } catch (Exception e) {
            BabelFish.LOGGER.warn("Failed to save babelfish-server.yaml: " + e.getMessage());
        }
    }

    private void loadTranslatorConfigs() {
        loadedTranslatorConfigs.clear();

        File dir = TRANSLATORS_DIR.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
            createExampleTranslatorConfig();
            return;
        }

        File[] jsonFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (jsonFiles == null || jsonFiles.length == 0) {
            createExampleTranslatorConfig();
            return;
        }

        for (File jsonFile : jsonFiles) {
            try (FileReader reader = new FileReader(jsonFile)) {
                TranslatorConfig config = GSON.fromJson(reader, TranslatorConfig.class);
                if (config != null && config.id != null) {
                    loadedTranslatorConfigs.add(config);
                }
            } catch (Exception e) {
                BabelFish.LOGGER.warn("Failed to load translator config from {}: {}", jsonFile.getName(), e.getMessage());
            }
        }

        BabelFish.LOGGER.info("Loaded {} translator configs", loadedTranslatorConfigs.size());

        for (TranslatorConfig config : loadedTranslatorConfigs) {
            if (config.id != null) {
                customTranslators.put(config.id, new CustomTranslator(config));
            }
        }

        TranslateService.initTranslator(customTranslators.get(selectedTranslator));
        BabelFish.LOGGER.info("Initialized translator: {}", selectedTranslator);
    }
}


