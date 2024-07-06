package com.edu.eduplatform.models;


public enum MaterialType {
    LECTURES("lectures"),
    LABS("labs"),
    VIDEOS("videos"),
    ASSIGNMENTS("assignments"),
    OTHERS("others");

    private final String folder;

    MaterialType(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }
}

