package dev.aullisia.babelfish;

import dev.aullisia.babelfish.translators.LibreTranslateTranslator;

import java.util.concurrent.CompletableFuture;

public class TranslateService {
    public record TranslateParams(String text, String from, String to) {
    }
    private static final LibreTranslateTranslator libreTranslateTranslator = new LibreTranslateTranslator();

    public static CompletableFuture<String> translateMessage(TranslateParams params) {
        BabelFish.LOGGER.info("Translating message from {} to {}: {}", params.from, params.to, params.text);
        if (params.from.equalsIgnoreCase(params.to)) {
            return CompletableFuture.completedFuture(params.text);
        }

        return libreTranslateTranslator.translate(params.text, params.from, params.to)
                .thenApply(result -> {
                    if (result.endsWith("\n")) {
                        return result.substring(0, result.length() - 1);
                    }
                    return result;
                })
                .exceptionally(ex -> {
                    BabelFish.LOGGER.warn(ex.getMessage());
                    return "[Translation error]";
                });
    }
}
