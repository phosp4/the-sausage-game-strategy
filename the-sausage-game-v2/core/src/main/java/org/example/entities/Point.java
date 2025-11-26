package org.example.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Point implements Comparable<Point> {
    private final int x;
    private final int y;

    public Point(int x, int y) {

        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public int compareTo(Point other) {
        int cmp = Integer.compare(this.x, other.x);
        if (cmp != 0) {
            return cmp;
        }
        return Integer.compare(this.y, other.y);
    }
}
