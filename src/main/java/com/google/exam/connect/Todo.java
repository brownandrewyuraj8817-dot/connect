package com.google.exam.connect;

import lombok.Data;

@Data
public class Todo {
    private String id;
    private String title;
    private boolean done;

    public Todo() {}

    public Todo(String id, String title, boolean done) {
        this.id = id;
        this.title = title;
        this.done = done;
    }

    // getters/setters...
}
