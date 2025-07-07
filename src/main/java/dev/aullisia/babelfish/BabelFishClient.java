package dev.aullisia.babelfish;

import dev.aullisia.babelfish.client.config.BabelFishClientConfig;
import dev.aullisia.babelfish.network.packet.SetLanguagePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

public class BabelFishClient implements ClientModInitializer {
    Integer joinCount = 0;

    @Override
    public void onInitializeClient() {
        BabelFishClientConfig.load();

        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
                ClientPlayNetworking.send(new SetLanguagePayload(BabelFishClientConfig.get().preferredLang));
            });
        } else {
            ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
                joinCount++;
                if (joinCount == 1) {
                    ClientPlayNetworking.send(new SetLanguagePayload("en"));
                } else {
                    ClientPlayNetworking.send(new SetLanguagePayload("nl"));
                }
            });
        }
    }
}
