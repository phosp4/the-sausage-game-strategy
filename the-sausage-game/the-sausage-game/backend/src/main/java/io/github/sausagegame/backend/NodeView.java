package io.github.sausagegame.backend;

/**
 * Immutable snapshot of a board node exposed to clients.
 */
public record NodeView(int id, int row, int column, float x, float y, boolean occupied, Player occupant) {
}
