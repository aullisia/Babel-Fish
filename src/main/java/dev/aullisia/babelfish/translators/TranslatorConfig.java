package dev.aullisia.babelfish.translators;

import java.util.Map;

public class TranslatorConfig {
    public String id;
    public String url;
    public String method = "POST";
    public Map<String, String> headers;
    public Object body;
    public String responsePath;
}