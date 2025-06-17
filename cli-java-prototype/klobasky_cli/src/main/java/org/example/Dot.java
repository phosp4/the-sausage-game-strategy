package org.example;

import lombok.Getter;

@Getter
public class Dot {
    private int x;
    private int y;

    public Dot(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates cannot be negative");
        }
        // todo check if the coordinates are valid according to offset
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return STR."(\{x},\{y})";
    }
}