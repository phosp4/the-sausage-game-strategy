package org.example.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@Data
@EqualsAndHashCode
public class Sausage implements Comparable<Sausage> {
//    private Set<Point> threePoints = new HashSet<>();
    private Set<Point> threePoints = new TreeSet<>(); // temporary, for better printing
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
    public int compareTo(Sausage sausage) {
        Iterator<Point> it1 = this.threePoints.iterator();
        Iterator<Point> it2 = sausage.threePoints.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            Point p1 = it1.next();
            Point p2 = it2.next();
            int cmp = p1.compareTo(p2);
            if (cmp != 0) {
                return cmp;
            }
        }
        // If all points are equal so far, compare set sizes (should be 3, but for safety)
        return Integer.compare(this.threePoints.size(), sausage.threePoints.size());
    }
}
