package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dependency {
    private String source;
    private String target;
    private String label;
    private String method;

    public Dependency(String source, String target, String label) {
        this.source = source;
        this.target = target;
        this.label = label;
        this.method = null;
    }
}

