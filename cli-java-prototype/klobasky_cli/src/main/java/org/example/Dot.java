package org.example;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dot implements Comparable<Dot> {
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
        return "(" + x + "," + y + ")";
    }

    @Override
    public int compareTo(Dot other) {
        if (this.x != other.x) {
            return Integer.compare(this.x, other.x);
        }
        return Integer.compare(this.y, other.y);
    }
}