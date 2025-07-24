package dev.aullisia.babelfish;

import dev.aullisia.babelfish.network.BabelFishNetwork;
import dev.aullisia.babelfish.config.server.BabelFishServerConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BabelFish implements ModInitializer {
	public static final String MOD_ID = "babelfish";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public BabelFishServerConfig ServerConfig;

	@Override
	public void onInitialize() {
		LOGGER.info("Babel Fish Initialised");
		BabelFishNetwork.register();

		BabelFishServerConfig.load();
		var serverConfig = BabelFishServerConfig.getInstance();
		LOGGER.info("Loaded server config. Default language: {}", serverConfig.defaultLanguage);

		ServerPlayConnectionEvents.JOIN.register((handler, packetSender, server) -> {
			server.execute(() -> {
				ServerPlayerEntity player = handler.getPlayer();
				if (TranslationPreferences.hasPreferences(player.getUuid())) return;
				TranslationPreferences.forceSetLanguage(player, BabelFishServerConfig.getInstance().defaultLanguage);
			});
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			var uuid = player.getUuid();
			TranslationPreferences.removePlayer(uuid);
		});
	}
}