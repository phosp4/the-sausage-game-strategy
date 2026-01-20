package org.example.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
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

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }
}
