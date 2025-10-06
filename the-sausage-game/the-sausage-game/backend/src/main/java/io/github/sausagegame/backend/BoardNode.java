package io.github.sausagegame.backend;

import java.util.Collections;
import java.util.List;

final class BoardNode {
    private final int id;
    private final GridPosition position;
    private final float normalizedX;
    private final float normalizedY;
    private List<Integer> neighbors = List.of();
    private Player occupant;

    BoardNode(int id, GridPosition position, float normalizedX, float normalizedY) {
        this.id = id;
        this.position = position;
        this.normalizedX = normalizedX;
        this.normalizedY = normalizedY;
    }

    int id() {
        return id;
    }

    GridPosition position() {
        return position;
    }

    float x() {
        return normalizedX;
    }

    float y() {
        return normalizedY;
    }

    boolean isOccupied() {
        return occupant != null;
    }

    Player occupant() {
        return occupant;
    }

    void occupy(Player player) {
        this.occupant = player;
    }

    void clear() {
        this.occupant = null;
    }

    void setNeighbors(List<Integer> neighbors) {
        this.neighbors = List.copyOf(neighbors);
    }

    List<Integer> neighbors() {
        return Collections.unmodifiableList(neighbors);
    }
}
