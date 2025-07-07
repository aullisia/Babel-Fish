package dev.aullisia.babelfish;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TranslationPreferences {
    private static final Map<UUID, String> playerLanguages = new ConcurrentHashMap<>();
    private static final Set<String> activeLanguages = ConcurrentHashMap.newKeySet();

    public static void setLanguage(UUID playerId, String language) {
        String oldLang = playerLanguages.put(playerId, language);
        activeLanguages.add(language);

        if (oldLang != null && !oldLang.equals(language) && !playerLanguages.containsValue(oldLang)) {
            activeLanguages.remove(oldLang);
        }
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
