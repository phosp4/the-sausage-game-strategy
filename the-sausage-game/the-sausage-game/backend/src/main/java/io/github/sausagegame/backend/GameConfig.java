package io.github.sausagegame.backend;

/**
 * Immutable configuration describing the logical size of the board.
 */
public record GameConfig(int columns, int rows) {

    public GameConfig {
        if (columns < 3) {
            throw new IllegalArgumentException("The board must contain at least three columns");
        }
        if (rows < 1) {
            throw new IllegalArgumentException("The board must contain at least one row");
        }
    }
}
