package io.github.sausagegame.backend;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Cycles through the registered players and keeps track of whose turn it is.
 */
public final class TurnManager {
    private final List<Player> players;
    private int index = 0;

    public TurnManager(List<Player> players) {
        if (players == null || players.size() < 2) {
            throw new IllegalArgumentException("At least two players are required");
        }
        this.players = List.copyOf(players);
    }

    public Player currentPlayer() {
        return players.get(index);
    }

    public Player otherPlayer() {
        return players.get((index + 1) % players.size());
    }

    public void nextTurn() {
        index = (index + 1) % players.size();
    }

    public List<Player> players() {
        return Collections.unmodifiableList(players);
    }
}
