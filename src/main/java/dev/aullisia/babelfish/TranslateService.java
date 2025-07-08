package dev.aullisia.babelfish;

import dev.aullisia.babelfish.translators.BaseTranslator;

import java.util.concurrent.CompletableFuture;

public class TranslateService {
    public record TranslateParams(String text, String from, String to) {
    }

    private static BaseTranslator translator = null;

    public static void initTranslator(BaseTranslator newTranslator) {
        translator = newTranslator;
    }

    public static CompletableFuture<String> translateMessage(TranslateParams params) {
        if (translator == null) return CompletableFuture.completedFuture("[Translation error]");

        BabelFish.LOGGER.info("Translating message from {} to {}: {}", params.from, params.to, params.text);
        if (params.from.equalsIgnoreCase(params.to)) {
            return CompletableFuture.completedFuture(params.text);
        }

        return translator.translate(params.text, params.from, params.to)
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
