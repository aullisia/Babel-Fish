package dev.aullisia.babelfish.translators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aullisia.babelfish.BabelFish;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class CustomTranslator extends BaseTranslator {
    private final TranslatorConfig config;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomTranslator(TranslatorConfig config) {
        this.config = config;
    }

    @Override
    public CompletableFuture<String> translate(String text, String sourceLang, String targetLang) {
        try {
            String rawBodyJson = objectMapper.writeValueAsString(config.body);
            String interpolatedBody = rawBodyJson
                    .replace("${text}", text)
                    .replace("${sourceLang}", sourceLang)
                    .replace("${targetLang}", targetLang);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(config.url))
                    .method(config.method.toUpperCase(),
                            HttpRequest.BodyPublishers.ofString(interpolatedBody, StandardCharsets.UTF_8));

            if (config.headers != null) {
                config.headers.forEach(requestBuilder::header);
            }

            HttpRequest request = requestBuilder.build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(this::extractFromResponse)
                    .exceptionally(ex -> {
                        BabelFish.LOGGER.warn(ex.getMessage());
                        return "[Translation error]";
                    });

        } catch (Exception e) {
            BabelFish.LOGGER.warn(e.getMessage());
            CompletableFuture<String> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }

    private String extractFromResponse(String responseBody) {
        try {
            if (config.responsePath != null && !config.responsePath.isEmpty()) {
                String[] pathParts = config.responsePath.split("\\.");

                JsonNode currentNode = objectMapper.readTree(responseBody);

                for (String key : pathParts) {
                    if (currentNode != null) {
                        currentNode = currentNode.get(key);
                    } else {
                        break;
                    }
                }

                if (currentNode != null && currentNode.isValueNode()) {
                    return currentNode.asText();
                } else {
                    return "[Translation error: path not found or not a value]";
                }
            }

            return "[No responsePath defined]";
        } catch (Exception e) {
            BabelFish.LOGGER.warn("Failed to extract translation from response: {}", e.getMessage());
            return "[Translation parse error]";
        }
    }

}
