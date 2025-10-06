package org.example.entities;

import lombok.Getter;
import org.example.utils.CoordUtils;
import org.example.utils.GridConstants;

import java.util.*;

@Getter
public class Sausage {
    private Connection connection1;
    private Connection connection2;
    private Set<Dot> threeDots = new HashSet<Dot>();
    private int player;

    public Sausage(int player, Dot dot1, Dot dot2, Dot dot3) {

        if (dot1 == null || dot2 == null || dot3 == null) {
            throw new IllegalArgumentException("Dots cannot be null");
        }
        if (dot1.equals(dot2) || dot1.equals(dot3) || dot2.equals(dot3)) {
            throw new IllegalArgumentException("Dots must be unique");
        }
        if (player != GridConstants.PLAYER_ONE && player != GridConstants.PLAYER_TWO) {
            throw new IllegalArgumentException("Invalid player");
        }

        // Check if the dots are neighbors
        int neighborCount = 0;
        List<Connection> connections = new ArrayList<>();

        if (CoordUtils.areNeighbors(dot1, dot2)) {
            neighborCount++;
            connections.add(new Connection(dot1, dot2));
        }
        if (CoordUtils.areNeighbors(dot2, dot3)) {
            neighborCount++;
            connections.add(new Connection(dot2, dot3));
        }
        if (CoordUtils.areNeighbors(dot1, dot3)) {
            neighborCount++;
            connections.add(new Connection(dot1, dot3));
        }

        if (neighborCount < 2) {
            throw new IllegalArgumentException("Dots must be neighbors in a valid sausage shape");
        }

        threeDots.add(dot1);
        threeDots.add(dot2);
        threeDots.add(dot3);
        this.player = player;
        connection1 = connections.get(0);
        connection2 = connections.get(1);
        // if there are more than 2 connections, we can ignore the third one
    }

    public Sausage(int player, Connection conn1, Connection conn2) {

        if (conn1 == null || conn2 == null) {
            throw new IllegalArgumentException("Connections cannot be null");
        }
        if (player != GridConstants.PLAYER_ONE && player != GridConstants.PLAYER_TWO) {
            throw new IllegalArgumentException("Invalid player");
        }

        this.player = player;
        threeDots.add(conn1.getA());
        threeDots.add(conn1.getB());
        threeDots.add(conn2.getA());
        threeDots.add(conn2.getB());

        if (threeDots.size() != 3) {
            throw new IllegalArgumentException("Connections must form a valid sausage with exactly 3 unique dots");
        }
        this.connection1 = conn1;
        this.connection2 = conn2;
    }

    @Override
    public String toString() {
        return "{" +
                "dots=" + threeDots +
                ", player=" + player +
                '}';
    }
}
