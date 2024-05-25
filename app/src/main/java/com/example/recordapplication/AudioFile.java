package com.example.recordapplication;

public class AudioFile {
    private int id;
    private String name;

    public AudioFile(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
