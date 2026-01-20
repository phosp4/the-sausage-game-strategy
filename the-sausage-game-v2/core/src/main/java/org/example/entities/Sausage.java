package org.example.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@Data
//@EqualsAndHashCode
public class Sausage {
//    private Set<Point> threePoints = new HashSet<>();
    private List<Point> threePoints = new ArrayList<>();
    private Player player;

    public Sausage(Player player, Point p1, Point p2, Point p3) {

        if (p1 == null || p2 == null || p3 == null) {
            throw new IllegalArgumentException("Dots cannot be null");
        }

        threePoints.add(p1);
        threePoints.add(p2);
        threePoints.add(p3);

        this.player = player;
    }

    public Sausage(Point p1, Point p2, Point p3) {
        this(null, p1, p2, p3);
    }

    @Override
    public String toString() {
        return "{" + threePoints + ", " + player + '}';
    }

    @Override
    public int hashCode() {
        return new HashSet<>(threePoints).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sausage sausage = (Sausage) o;
        return new HashSet<>(threePoints).equals(new HashSet<>(sausage.threePoints));
    }
}
