package com.example;

import com.example.exporter.CytoscapeCSVExporter;
import com.example.exporter.GraphvizExporter;
import com.example.model.Dependency;
import com.example.model.RepoScanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {

        Path repoRoot;
        Path outputDir;

        if (args.length < 2) {
            System.out.println("No arguments detected. Running in IntelliJ mode...");
            repoRoot = Path.of("/home/utkarsh/Desktop/ftgo-application");
            outputDir = repoRoot.resolve("out");
        } else {
            repoRoot = Path.of(args[0]);
            outputDir = Path.of(args[1]);
        }

        Files.createDirectories(outputDir);

        RepoScanner scanner = new RepoScanner(repoRoot);
        List<Dependency> dependencies = scanner.scan();

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(outputDir.resolve("cytoscape.json").toFile(), dependencies);

        // Add Cytoscape CSV export
        CytoscapeCSVExporter.export(dependencies, outputDir.resolve("dependencies.csv").toString());
        GraphvizExporter.export(dependencies, outputDir.resolve("graph.dot").toFile());
        System.out.println("Cytoscape file generated at: " + outputDir);
    }

    private static Map<String, Object> toCytoscape(List<Dependency> deps) {

        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();

        Set<String> nodeSet = new HashSet<>();

        for (Dependency d : deps) {
            nodeSet.add(d.getSource());
            nodeSet.add(d.getTarget());
        }

        for (String n : nodeSet) {
            nodes.add(Map.of("data", Map.of("id", n, "label", n)));
        }

        int edgeId = 1;
        for (Dependency d : deps) {
            edges.add(Map.of(
                    "data", Map.of(
                            "id", "e" + edgeId++,
                            "source", d.getSource(),
                            "target", d.getTarget(),
                            "label", d.getLabel()
                    )
            ));
        }

        return Map.of("elements", Map.of(
                "nodes", nodes,
                "edges", edges
        ));
    }
}
