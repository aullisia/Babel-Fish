//package dev.aullisia.babelfish.translators;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.nio.charset.StandardCharsets;
//import java.util.concurrent.CompletableFuture;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import dev.aullisia.babelfish.BabelFish;
//
//public class LibreTranslateTranslator extends BaseTranslator {
//    private static final String API_URL = "http://localhost:5000/translate";
//
//    private final HttpClient httpClient = HttpClient.newHttpClient();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public CompletableFuture<String> translate(String text, String sourceLang, String targetLang) {
//        try {
//            String requestBody = objectMapper.writeValueAsString(new TranslationRequest(text, sourceLang, targetLang));
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(API_URL))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
//                    .build();
//
//            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                    .thenApply(HttpResponse::body)
//                    .thenApply(this::parseTranslation);
//
//        } catch (Exception e) {
//            CompletableFuture<String> failed = new CompletableFuture<>();
//            failed.completeExceptionally(e);
//            return failed;
//        }
//    }
//
//    private String parseTranslation(String responseBody) {
//        try {
//            JsonNode root = objectMapper.readTree(responseBody);
//            JsonNode translatedTextNode = root.get("translatedText");
//            if (translatedTextNode == null) {
//                BabelFish.LOGGER.warn("Warning: 'translatedText' field not found in response: {}", responseBody);
//                return "[Translation error: missing field]";
//            }
//            return translatedTextNode.asText();
//        } catch (Exception e) {
//            BabelFish.LOGGER.warn(e.getMessage());
//            return "[Translation error: parse failure]";
//        }
//    }
//
//    private record TranslationRequest(String q, String source, String target) {
//    }
//}
