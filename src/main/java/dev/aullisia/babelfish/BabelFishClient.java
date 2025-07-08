package dev.aullisia.babelfish;

import dev.aullisia.babelfish.config.client.BabelFishClientConfig;
import dev.aullisia.babelfish.network.packet.SetLanguagePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class BabelFishClient implements ClientModInitializer {
    Integer joinCount = 0;

    @Override
    public void onInitializeClient() {
        BabelFishClientConfig.load();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ClientPlayNetworking.send(new SetLanguagePayload(BabelFishClientConfig.get().preferredLang));
        });
    }
}
