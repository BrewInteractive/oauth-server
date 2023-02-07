package com.brew.oauth20.server.model;
public class GreetingModel {
    public GreetingModel(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long id;
    public String content;
}