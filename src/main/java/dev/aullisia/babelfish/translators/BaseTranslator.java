package dev.aullisia.babelfish.translators;

import java.util.concurrent.CompletableFuture;

public abstract class BaseTranslator {
    public abstract CompletableFuture<String> translate(String text, String sourceLang, String targetLang);
}
