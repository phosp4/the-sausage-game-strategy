package io.github;

import com.badlogic.gdx.graphics.Color;

public class GridCircle {
    float x, y;
    int row, col;
    float baseRadius = 15f;
    float enlargedRadius = 30f;
    float currentRadius = 15f;
    Color color = Color.BLACK;
    boolean connected = false;

    GridCircle(float x, float y, int row, int col) {
        this.x = x;
        this.y = y;
        this.row = row;
        this.col = col;
    }

    /**
     * Updates the circle's hover state and radius based on mouse position and touch state.
     */
    boolean update(float mouseX, float mouseY, boolean isTouched, TurnManager turnManager) {
        boolean isHovered = Math.hypot(mouseX - x, mouseY - y) <= currentRadius;
        if (isHovered) {
            color = connected ? Color.GRAY : turnManager.getCurrentPlayer().getColor();
            currentRadius = isTouched ? enlargedRadius : baseRadius;
            return true;
        } else {
            color = connected ? Color.GRAY : Color.BLACK;
            currentRadius = baseRadius;
            return false;
        }
    }

    boolean isNeighbor(GridCircle other) {

        // coords of the 8 neighbors - in hex grid they vary based on row parity
        int[][] offsetsEven = {
            {-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 0},
            {-2, 0}, {2, 0}
        };
        int[][] offsetsOdd = {
            {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, 0}, {1, 1},
            {-2, 0}, {2, 0}
        };

        // Determine which set of offsets to use based on the row parity
        int[][] offsets = (row % 2 == 0) ? offsetsEven : offsetsOdd;

        // Check if the other circle is a neighbor
        for (int[] offset : offsets) {
            int nr = row + offset[0];
            int nc = col + offset[1];
            if (other.row == nr && other.col == nc) {
                return true;
            }
        }
        return false;
    }
}
