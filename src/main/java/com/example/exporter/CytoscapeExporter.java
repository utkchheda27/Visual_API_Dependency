package com.example.exporter;

import com.example.model.Dependency;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CytoscapeExporter {

    public static void export(List<Dependency> deps, File outFile) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();
        ArrayNode elements = mapper.createArrayNode();
        root.set("elements", elements);

        Set<String> addedNodes = new HashSet<>();

        // Add nodes
        for (Dependency d : deps) {

            if (d.getSource() != null && !addedNodes.contains(d.getSource())) {
                ObjectNode node = mapper.createObjectNode();
                ObjectNode data = mapper.createObjectNode();
                data.put("id", d.getSource());
                data.put("label", d.getSource());
                node.set("data", data);
                elements.add(node);

                addedNodes.add(d.getSource());
            }

            if (d.getTarget() != null && !addedNodes.contains(d.getTarget())) {
                ObjectNode node = mapper.createObjectNode();
                ObjectNode data = mapper.createObjectNode();
                data.put("id", d.getTarget());
                data.put("label", d.getTarget());
                node.set("data", data);
                elements.add(node);

                addedNodes.add(d.getTarget());
            }
        }

        // Add edges
        int edgeCounter = 0;
        for (Dependency d : deps) {
            if (d.getSource() == null || d.getTarget() == null) continue;

            ObjectNode edge = mapper.createObjectNode();
            ObjectNode data = mapper.createObjectNode();

            data.put("id", "e" + (++edgeCounter));
            data.put("source", d.getSource());
            data.put("target", d.getTarget());

            String label = d.getLabel() != null ? d.getLabel() : "";
            data.put("label", label);

            edge.set("data", data);
            elements.add(edge);
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(outFile, root);
    }
}
