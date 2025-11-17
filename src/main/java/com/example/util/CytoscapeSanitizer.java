package com.example.util;

public class CytoscapeSanitizer {

    private static int emptyCounter = 1;

    public static String cleanId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return "empty_" + (emptyCounter++);
        }

        // Remove all invalid characters
        String cleaned = id.replaceAll("[^A-Za-z0-9_]", "_");

        // Ensure it doesn't become empty after cleaning
        if (cleaned.trim().isEmpty()) {
            cleaned = "empty_" + (emptyCounter++);
        }

        return cleaned;
    }
}
