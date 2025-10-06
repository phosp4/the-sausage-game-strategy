package io.github.sausagegame.backend;

record GameConnection(int fromNodeId, int toNodeId, Player owner) {
    GameConnection {
        if (fromNodeId == toNodeId) {
            throw new IllegalArgumentException("Connection endpoints must be different");
        }
    }
}
