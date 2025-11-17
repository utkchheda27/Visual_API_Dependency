package com.example.parser;

import com.example.model.Dependency;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class OpenApiParser {

    public static List<Dependency> parse(Path p, String sourceService) {
        List<Dependency> list = new ArrayList<>();

        try {
            String text = Files.readString(p);

            if (p.toString().endsWith(".json")) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(text);

                if (root.has("servers")) {
                    for (JsonNode s : root.get("servers")) {
                        String host = extractHost(s.path("url").asText(""));
                        JsonNode paths = root.get("paths");

                        if (paths != null) {
                            paths.fieldNames().forEachRemaining(ep ->
                                    list.add(new Dependency(sourceService, host, ep, null)));
                        }
                    }
                }

            } else {
                Yaml yaml = new Yaml();
                Object obj = yaml.load(text);

                if (obj instanceof Map map) {
                    Object servers = map.get("servers");
                    if (servers instanceof List listOfServers) {
                        for (Object s : listOfServers) {
                            if (s instanceof Map sm) {
                                String host = extractHost(sm.get("url").toString());
                                Map<Object, Object> paths = (Map<Object, Object>) map.get("paths");

                                if (paths != null) {
                                    for (Object ep : paths.keySet()) {
                                        list.add(new Dependency(sourceService, host, ep.toString(), null));
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception ignored) {}

        return list;
    }

    private static String extractHost(String url) {
        String trimmed = url.replace("http://", "").replace("https://", "");
        int idx = trimmed.indexOf('/');
        return (idx > 0) ? trimmed.substring(0, idx) : trimmed;
    }
}
