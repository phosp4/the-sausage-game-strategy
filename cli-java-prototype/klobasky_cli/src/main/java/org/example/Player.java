package org.example;

import lombok.Data;

@Data
public class Player {
    private String name;

    public Player(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Player{name='" + name + "'}";
    }
}