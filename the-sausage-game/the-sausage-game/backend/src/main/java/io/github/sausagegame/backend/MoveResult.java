package io.github.sausagegame.backend;

import java.util.List;

/**
 * Rich information about the outcome of a move attempt.
 */
public record MoveResult(
        MoveStatus status,
        Player actingPlayer,
        Player nextPlayer,
        Player winner,
        List<ConnectionView> createdConnections,
        String message
) {
}
