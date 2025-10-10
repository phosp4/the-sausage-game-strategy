package org.example.archive;

public class Dot implements Comparable<Dot> {
    private int x; // x coordinate in offset coordinates
    private int y; // y coordinate in offset coordinates

    public Dot(int xInOffset, int yInOffset) {
        if (xInOffset < 0 || yInOffset < 0) {
            throw new IllegalArgumentException("Coordinates cannot be negative");
        }
        // todo check if the coordinates are valid according to offset
        this.x = xInOffset;
        this.y = yInOffset;
    }

    public int getOffsetX() {
        return x;
    }
    public int getOffsetY() {
        return y;
    }

    public int getDoubledX() {
        return CoordUtils.offsetToDoubledX(x,y);
    }
    public int getDoubledY() {
        return CoordUtils.offsetToDoubledY(x,y);
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