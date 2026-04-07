package org.example.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.*;

@Data
//@EqualsAndHashCode
public class Sausage implements Serializable {
//    private Set<Point> threePoints = new HashSet<>();
    private List<Point> threePoints = new ArrayList<>();
    transient private Player player; // neuklada sa pri serializacii

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

//    public boolean intersects(Sausage another) {
//        for (Point pA : threePoints) {
//            for (Point pB : another.threePoints) {
//                if (pA.equals(pB)) {
//                    return true;
//                }
//            }
//        }
//
//
//
//        // ++ ak sa akokolvek pretinaju, return true
////        int[] xCoords = {threePoints.get(0).getX(), threePoints.get(1).getX(), threePoints.get(2).getX()};
//
//        return false;
//    }

//    public static void main(String[] args) {
//        Sausage s1 = new Sausage(new Point(0,0), new Point(0,2), new Point(0,4));
//        Sausage s2 = new Sausage(new Point(1,0), new Point(2,0), new Point(4,0));
//
//        System.out.println(s1.intersects(s2));
//    }

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
