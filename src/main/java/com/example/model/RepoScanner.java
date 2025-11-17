package com.example.model;

import com.example.util.CytoscapeSanitizer;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RequiredArgsConstructor
public class RepoScanner {

    private final Path root;

    public List<Dependency> scan() throws IOException {
        List<Dependency> list = new ArrayList<>();

        Files.walk(root)
                .filter(path -> Files.isRegularFile(path)
                        && isSupported(path))
                .forEach(path -> {
                    try {
                        processFile(path, list);
                    } catch (Exception ignored) {}
                });

        // Deduplicate
        return dedupe(list);
    }

    private boolean isSupported(Path p) {
        String f = p.toString().toLowerCase();
        return f.endsWith(".java") || f.endsWith(".json") || f.endsWith(".yaml") || f.endsWith(".yml");
    }

    private void processFile(Path file, List<Dependency> list) throws IOException {

        String fileName = file.getFileName().toString();
        String service = CytoscapeSanitizer.cleanId(root.relativize(file.getParent()).toString());

        List<String> lines = Files.readAllLines(file);

        for (String line : lines) {
            if (line.contains("http")) {

                String cleanedTarget = CytoscapeSanitizer.cleanId(extractServiceName(line));
                String cleanedSource = CytoscapeSanitizer.cleanId(service);

                list.add(new Dependency(cleanedSource, cleanedTarget, "/"));
            }
        }
    }

    private String extractServiceName(String line) {
        int start = line.indexOf("http");
        if (start == -1) return "unknown";

        String url = line.substring(start).split("[\"'\\s]")[0];
        return url.replace("http://", "").replace("https://", "")
                .split("/")[0]
                .replaceAll("[^A-Za-z0-9_]", "_");
    }

    private List<Dependency> dedupe(List<Dependency> list) {
        Set<String> set = new HashSet<>();
        List<Dependency> out = new ArrayList<>();

        for (Dependency d : list) {
            String key = d.getSource() + "|" + d.getTarget() + "|" + d.getLabel();
            if (set.add(key)) out.add(d);
        }
        return out;
    }
}
