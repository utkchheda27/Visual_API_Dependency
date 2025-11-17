package com.example.exporter;

import com.example.model.Dependency;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class GraphvizExporter {

    public static void export(List<Dependency> deps, File out) {
        try (FileWriter fw = new FileWriter(out)) {
            fw.write("digraph G {\n");
            fw.write("  rankdir=LR;\n");
            fw.write("  node [shape=box, style=rounded];\n");

            for (Dependency d : deps) {
                fw.write(String.format(
                        "  \"%s\" -> \"%s\" [label=\"%s\"];\n",
                        d.getSource(), d.getTarget(), d.getLabel()
                ));
            }

            fw.write("}\n");
        } catch (Exception ignored) {}
    }
}
