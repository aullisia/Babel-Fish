package dev.aullisia.babelfish.config.server;

import dev.aullisia.babelfish.BabelFish;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static dev.aullisia.babelfish.config.server.BabelFishServerConfig.TRANSLATORS_DIR;

public class ServerTranslatorConfigUtils {
    private static final String EXAMPLE_CONFIG_PATH = "assets/" + BabelFish.MOD_ID + "/template/example-config.json";
    public static void createExampleTranslatorConfig() {
        try {
            Files.createDirectories(TRANSLATORS_DIR);
            Path targetFile = TRANSLATORS_DIR.resolve("example-config.json");

            if (Files.exists(targetFile)) {
                BabelFish.LOGGER.info("Example translator config already exists at {}", targetFile);
                return;
            }

            try (InputStream resourceStream = ServerTranslatorConfigUtils.class.getClassLoader().getResourceAsStream(EXAMPLE_CONFIG_PATH)) {
                if (resourceStream == null) {
                    BabelFish.LOGGER.warn("Example translator config resource not found: {}", EXAMPLE_CONFIG_PATH);
                    return;
                }
                Files.copy(resourceStream, targetFile);
                BabelFish.LOGGER.info("Copied example translator config to {}", targetFile);
            } catch (IOException e) {
                BabelFish.LOGGER.warn("Failed to copy example translator config: {}", e.getMessage());
            }
        } catch (IOException e) {
            BabelFish.LOGGER.warn("Failed to create example translator config: {}", e.getMessage());
        }
    }


    public static class NoTagRepresenter extends Representer {
        public NoTagRepresenter() {
            super(new DumperOptions());
            this.addClassTag(BabelFishServerConfig.class, Tag.MAP);
        }
    }
}
