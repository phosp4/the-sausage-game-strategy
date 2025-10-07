package org.example.entities;

import lombok.Getter;
import org.example.utils.CoordUtils;

import java.util.*;

@Getter
public class Sausage {
    private Set<Dot> threeDots = new HashSet<Dot>();
    private Player player;

    public Sausage(Player player, Dot dot1, Dot dot2, Dot dot3) {

        if (dot1 == null || dot2 == null || dot3 == null) {
            throw new IllegalArgumentException("Dots cannot be null");
        }
        if (dot1.equals(dot2) || dot1.equals(dot3) || dot2.equals(dot3)) {
            throw new IllegalArgumentException("Dots must be unique");
        }
//
//        // Check if the dots are neighbors
//        int neighborCount = 0;
//        List<Connection> connections = new ArrayList<>();
//
//        if (CoordUtils.areNeighbors(dot1, dot2)) {
//            neighborCount++;
//            connections.add(new Connection(dot1, dot2));
//        }
//        if (CoordUtils.areNeighbors(dot2, dot3)) {
//            neighborCount++;
//            connections.add(new Connection(dot2, dot3));
//        }
//        if (CoordUtils.areNeighbors(dot1, dot3)) {
//            neighborCount++;
//            connections.add(new Connection(dot1, dot3));
//        }
//
//        if (neighborCount < 2) {
//            throw new IllegalArgumentException("Dots must be neighbors in a valid sausage shape");
//        }

        threeDots.add(dot1);
        threeDots.add(dot2);
        threeDots.add(dot3);
        this.player = player;
    }

    @Override
    public String toString() {
        return "{" +
                "dots=" + threeDots +
                ", player=" + player +
                '}';
    }
}
