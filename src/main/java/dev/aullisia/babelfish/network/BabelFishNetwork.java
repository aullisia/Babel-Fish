package dev.aullisia.babelfish.network;

import dev.aullisia.babelfish.BabelFish;
import dev.aullisia.babelfish.TranslationPreferences;
import dev.aullisia.babelfish.network.packet.SetLanguagePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playC2S;

public class BabelFishNetwork {
    public static void register() {
        playC2S().register(SetLanguagePayload.ID, SetLanguagePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SetLanguagePayload.ID, (payload, context) -> {
            String language = payload.language();

            Objects.requireNonNull(context.player().getServer()).execute(() -> {
                UUID uuid = context.player().getUuid();
                TranslationPreferences.setLanguage(uuid, language);
                BabelFish.LOGGER.info("Set language for player {} to {}", context.player().getName().getString(), language);
            });
        });
    }
}
