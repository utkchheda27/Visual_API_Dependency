package com.example.exporter;

import com.example.model.Dependency;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CytoscapeCSVExporter {

    public static void export(List<Dependency> deps, String outputPath) throws IOException {
        FileWriter fw = new FileWriter(outputPath);

        // CSV HEADER
        fw.write("id,source,target,label,method\n");

        int id = 1;

        for (Dependency d : deps) {
            fw.write(
                    id++ + "," +
                            escape(d.getSource()) + "," +
                            escape(d.getTarget()) + "," +
                            escape(d.getLabel()) + "," +
                            escape(d.getMethod()) + "\n"
            );
        }

        fw.close();
        System.out.println("CSV exported to: " + outputPath);
    }

    private static String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
