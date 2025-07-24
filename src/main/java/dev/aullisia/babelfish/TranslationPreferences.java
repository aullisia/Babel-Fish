package dev.aullisia.babelfish;

import dev.aullisia.babelfish.config.server.BabelFishServerConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TranslationPreferences {
    private static final Map<UUID, String> playerLanguages = new ConcurrentHashMap<>();
    private static final Set<String> activeLanguages = ConcurrentHashMap.newKeySet();

    public static void setLanguage(ServerPlayerEntity player, String language) {
        UUID playerId = player.getUuid();
        TranslateService.TranslateParams params = new TranslateService.TranslateParams("Hello, World!", "en", language);

        TranslateService.translateMessage(params).thenAccept(translated -> {
            var newLanguage = language;
            if (translated == null) {
                player.sendMessage(Text.literal("BabelFish Error: Invalid language code provided. Please adjust it in the mod settings. Falling back to server default language.").formatted(Formatting.DARK_RED));
                newLanguage = BabelFishServerConfig.getInstance().defaultLanguage;
            }

            String oldLang = playerLanguages.put(playerId, newLanguage);
            activeLanguages.add(newLanguage);

            if (oldLang != null && !oldLang.equals(newLanguage) && !playerLanguages.containsValue(oldLang)) {
                activeLanguages.remove(oldLang);
            }
        });
    }

    public static String getLanguage(UUID playerId) {
        return playerLanguages.getOrDefault(playerId, "en_us");
    }

    public static void removePlayer(UUID playerId) {
        String removedLang = playerLanguages.remove(playerId);
        if (removedLang != null && !playerLanguages.containsValue(removedLang)) {
            activeLanguages.remove(removedLang);
        }
    }

    public static Collection<UUID> getAllPlayers() {
        return playerLanguages.keySet();
    }

    public static Set<String> getAllLanguages() {
        return Collections.unmodifiableSet(activeLanguages);
    }

    public static Set<UUID> getPlayersByLanguage(String language) {
        Set<UUID> matchingPlayers = new HashSet<>();
        for (Map.Entry<UUID, String> entry : playerLanguages.entrySet()) {
            if (language.equals(entry.getValue())) {
                matchingPlayers.add(entry.getKey());
            }
        }
        return matchingPlayers;
    }
}
