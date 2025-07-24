package dev.aullisia.babelfish.network;

import dev.aullisia.babelfish.BabelFish;
import dev.aullisia.babelfish.TranslationPreferences;
import dev.aullisia.babelfish.network.packet.SetLanguagePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playC2S;

public class BabelFishNetwork {
    private static final AtomicInteger devJoinCounter = new AtomicInteger(0);

    public static void register() {
        playC2S().register(SetLanguagePayload.ID, SetLanguagePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SetLanguagePayload.ID, (payload, context) -> {
            final String requestedLanguage = payload.language();

            Objects.requireNonNull(context.player().getServer()).execute(() -> {

                String assignedLanguage = requestedLanguage;

                if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                    int playerIndex = devJoinCounter.getAndIncrement();
                    assignedLanguage = (playerIndex % 2 == 0) ? "en" : "nl";
                    BabelFish.LOGGER.info("[DEV] Automatically assigned {} to {}", assignedLanguage, context.player().getName().getString());
                }

                TranslationPreferences.setLanguage(context.player(), assignedLanguage);
                BabelFish.LOGGER.info("Set language for player {} to {}", context.player().getName().getString(), assignedLanguage);
            });
        });
    }
}
