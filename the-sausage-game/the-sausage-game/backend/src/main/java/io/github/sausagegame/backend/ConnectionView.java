package io.github.sausagegame.backend;

/**
 * Immutable snapshot of an existing connection on the board.
 */
public record ConnectionView(int fromNodeId, int toNodeId, Player owner) {
}
