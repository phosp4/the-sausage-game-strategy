package io.github.sausagegame.backend;

import java.util.Objects;
import java.util.UUID;

/**
 * Representation of a participant in the sausage game.
 */
public final class Player {
    private final String id;
    private final String displayName;

    public Player(String displayName) {
        this(UUID.randomUUID().toString(), displayName);
    }

    public Player(String id, String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }
        this.id = Objects.requireNonNull(id, "id");
        this.displayName = displayName;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;
        return id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
