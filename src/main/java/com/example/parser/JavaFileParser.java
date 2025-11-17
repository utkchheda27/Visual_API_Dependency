package com.example.parser;

import com.example.model.Dependency;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaFileParser {

    private static final Pattern URL_PATTERN =
            Pattern.compile("https?://[\\w\\-.:]+(/[\\w\\-./]*)?");

    public static List<Dependency> parse(Path file, String sourceService) {
        List<Dependency> deps = new ArrayList<>();

        try {
            String code = Files.readString(file, StandardCharsets.UTF_8);
            CompilationUnit cu = StaticJavaParser.parse(code);

            // RestTemplate & WebClient pattern matching
            cu.findAll(MethodCallExpr.class).forEach(mc ->
                    mc.getArguments().forEach(arg ->
                            extractStringLiteral(arg)
                                    .ifPresent(url -> deps.add(parseUrl(sourceService, url)))
                    )
            );

            // Fallback regex
            Matcher matcher = URL_PATTERN.matcher(code);
            while (matcher.find()) {
                deps.add(parseUrl(sourceService, matcher.group()));
            }

        } catch (Exception ignored) {}

        return deps;
    }

    private static Optional<String> extractStringLiteral(Expression e) {
        String s = e.toString();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return Optional.of(s.substring(1, s.length() - 1));
        }
        return Optional.empty();
    }

    private static Dependency parseUrl(String source, String url) {
        String cleaned = url.replace("http://", "").replace("https://", "");
        String[] parts = cleaned.split("/", 2);

        String target = parts[0];
        String endpoint = parts.length > 1 ? "/" + parts[1] : "/";

        return new Dependency(source, target, endpoint, null);
    }
}