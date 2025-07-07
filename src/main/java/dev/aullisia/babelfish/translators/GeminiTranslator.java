//package dev.aullisia.babelfish.translators;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//
//import java.nio.charset.StandardCharsets;
//import java.util.concurrent.CompletableFuture;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//
//public class GeminiTranslator extends BaseTranslator {
//
//    private static final String API_KEY = "";
//    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
//
//    private final HttpClient httpClient;
//    private final ObjectMapper objectMapper;
//
//    public GeminiTranslator() {
//        this.httpClient = HttpClient.newHttpClient();
//        this.objectMapper = new ObjectMapper();
//    }
//
//    @Override
//    public CompletableFuture<String> translate(String text, String sourceLang, String targetLang) {
//        String prompt = String.format(
//                "Translate this exactly from %s to %s. Output must be the translated text only. Do not include explanations, quotes, formatting, or any text other than the pure translation. Input: %s",
//                sourceLang, targetLang, text
//        );
//
//        ObjectNode partText = objectMapper.createObjectNode();
//        partText.put("text", prompt);
//
//        ObjectNode parts = objectMapper.createObjectNode();
//        parts.set("parts", objectMapper.createArrayNode().add(partText));
//
//        ObjectNode contentNode = objectMapper.createObjectNode();
//        contentNode.set("contents", objectMapper.createArrayNode().add(parts.get("parts")));
//
//        ObjectNode rootNode = objectMapper.createObjectNode();
//        ObjectNode contentObject = objectMapper.createObjectNode();
//        contentObject.set("parts", objectMapper.createArrayNode().add(partText));
//        rootNode.set("contents", objectMapper.createArrayNode().add(contentObject));
//
//        try {
//            String requestBody = objectMapper.writeValueAsString(rootNode);
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(API_URL + API_KEY))
//                    .header("Content-Type", "application/json; charset=UTF-8")
//                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
//                    .build();
//
//            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                    .thenApply(HttpResponse::body)
//                    .thenApply(this::extractTranslatedText)
//                    .exceptionally(ex -> {
//                        ex.printStackTrace();
//                        return "[Translation error]";
//                    });
//
//        } catch (Exception e) {
//            CompletableFuture<String> failed = new CompletableFuture<>();
//            failed.completeExceptionally(e);
//            return failed;
//        }
//    }
//
//    private String extractTranslatedText(String responseBody) {
//        try {
//            var root = objectMapper.readTree(responseBody);
//            var candidates = root.get("candidates");
//            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
//                var firstCandidate = candidates.get(0);
//                var content = firstCandidate.get("content");
//                if (content != null) {
//                    var parts = content.get("parts");
//                    if (parts != null && parts.isArray() && parts.size() > 0) {
//                        return parts.get(0).get("text").asText();
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "[No translation found]";
//    }
//}
//
