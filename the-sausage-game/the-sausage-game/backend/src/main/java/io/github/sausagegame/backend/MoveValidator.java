package io.github.sausagegame.backend;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class MoveValidator {
    private MoveValidator() {
    }

    static Validation validateFullMove(List<Integer> move, List<BoardNode> nodes, List<GameConnection> connections) {
        if (move == null || move.size() != 3) {
            return Validation.invalid("A move must consist of exactly three nodes");
        }
        BoardNode first = getNode(nodes, move.get(0));
        BoardNode second = getNode(nodes, move.get(1));
        BoardNode third = getNode(nodes, move.get(2));

        if (first.isOccupied() || second.isOccupied() || third.isOccupied()) {
            return Validation.invalid("All selected nodes must be free");
        }
        if (!isNeighbor(first, second) || !isNeighbor(second, third)) {
            return Validation.invalid("Selected nodes must be adjacent");
        }
        if (intersectsExistingConnection(first, second, connections, nodes)) {
            return Validation.invalid("First segment intersects an existing sausage");
        }
        if (intersectsExistingConnection(second, third, connections, nodes)) {
            return Validation.invalid("Second segment intersects an existing sausage");
        }
        if (intersectsExistingConnection(first, third, connections, nodes)) {
            return Validation.invalid("The sausage cannot cross another connection");
        }
        return Validation.valid();
    }

    static boolean playerHasValidMove(List<BoardNode> nodes, List<GameConnection> connections) {
        for (BoardNode first : nodes) {
            if (first.isOccupied()) {
                continue;
            }
            for (int secondId : first.neighbors()) {
                BoardNode second = nodes.get(secondId);
                if (second.isOccupied()) {
                    continue;
                }
                if (intersectsExistingConnection(first, second, connections, nodes)) {
                    continue;
                }
                for (int thirdId : second.neighbors()) {
                    if (thirdId == first.id()) {
                        continue;
                    }
                    BoardNode third = nodes.get(thirdId);
                    if (third.isOccupied()) {
                        continue;
                    }
                    if (intersectsExistingConnection(second, third, connections, nodes)) {
                        continue;
                    }
                    if (intersectsExistingConnection(first, third, connections, nodes)) {
                        continue;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    static Set<Integer> findContinuations(List<Integer> partialMove, List<BoardNode> nodes, List<GameConnection> connections) {
        List<Integer> normalized = partialMove == null ? List.of() : partialMove;
        if (normalized.isEmpty()) {
            Set<Integer> available = new HashSet<>();
            for (BoardNode node : nodes) {
                if (!node.isOccupied()) {
                    available.add(node.id());
                }
            }
            return available;
        }

        if (normalized.size() == 1) {
            BoardNode first = getNode(nodes, normalized.get(0));
            Set<Integer> allowed = new HashSet<>();
            for (int neighborId : first.neighbors()) {
                BoardNode neighbor = nodes.get(neighborId);
                if (!neighbor.isOccupied() && !intersectsExistingConnection(first, neighbor, connections, nodes)) {
                    allowed.add(neighbor.id());
                }
            }
            return allowed;
        }

        if (normalized.size() == 2) {
            BoardNode first = getNode(nodes, normalized.get(0));
            BoardNode second = getNode(nodes, normalized.get(1));
            if (!second.neighbors().contains(first.id())) {
                return Set.of();
            }
            if (intersectsExistingConnection(first, second, connections, nodes)) {
                return Set.of();
            }
            Set<Integer> allowed = new HashSet<>();
            for (int neighborId : second.neighbors()) {
                if (neighborId == first.id()) {
                    continue;
                }
                BoardNode candidate = nodes.get(neighborId);
                if (candidate.isOccupied()) {
                    continue;
                }
                if (intersectsExistingConnection(second, candidate, connections, nodes)) {
                    continue;
                }
                if (intersectsExistingConnection(first, candidate, connections, nodes)) {
                    continue;
                }
                allowed.add(candidate.id());
            }
            return allowed;
        }
        return Set.of();
    }

    private static BoardNode getNode(List<BoardNode> nodes, int id) {
        if (id < 0 || id >= nodes.size()) {
            throw new IllegalArgumentException("Unknown node id: " + id);
        }
        return nodes.get(id);
    }

    private static boolean isNeighbor(BoardNode a, BoardNode b) {
        return a.neighbors().contains(b.id());
    }

    private static boolean intersectsExistingConnection(BoardNode a, BoardNode b, List<GameConnection> connections, List<BoardNode> nodes) {
        for (GameConnection connection : connections) {
            if (sharesEndpoint(a, b, connection)) {
                continue;
            }
            BoardNode c = nodes.get(connection.fromNodeId());
            BoardNode d = nodes.get(connection.toNodeId());
            if (segmentsIntersect(a.x(), a.y(), b.x(), b.y(), c.x(), c.y(), d.x(), d.y())) {
                return true;
            }
        }
        return false;
    }

    private static boolean sharesEndpoint(BoardNode a, BoardNode b, GameConnection connection) {
        return connection.fromNodeId() == a.id() || connection.fromNodeId() == b.id()
                || connection.toNodeId() == a.id() || connection.toNodeId() == b.id();
    }

    static boolean segmentsIntersect(float x1, float y1, float x2, float y2,
                                     float x3, float y3, float x4, float y4) {
        int o1 = orientation(x1, y1, x2, y2, x3, y3);
        int o2 = orientation(x1, y1, x2, y2, x4, y4);
        int o3 = orientation(x3, y3, x4, y4, x1, y1);
        int o4 = orientation(x3, y3, x4, y4, x2, y2);

        if (o1 != o2 && o3 != o4) return true;

        if (o1 == 0 && onSegment(x1, y1, x3, y3, x2, y2)) return true;
        if (o2 == 0 && onSegment(x1, y1, x4, y4, x2, y2)) return true;
        if (o3 == 0 && onSegment(x3, y3, x1, y1, x4, y4)) return true;
        return o4 == 0 && onSegment(x3, y3, x2, y2, x4, y4);
    }

    private static int orientation(float x1, float y1, float x2, float y2, float x3, float y3) {
        float val = (y2 - y1) * (x3 - x2) - (x2 - x1) * (y3 - y2);
        if (Math.abs(val) < 1e-6) return 0;
        return val > 0 ? 1 : 2;
    }

    private static boolean onSegment(float x1, float y1, float x2, float y2, float x3, float y3) {
        return x2 <= Math.max(x1, x3) && x2 >= Math.min(x1, x3)
                && y2 <= Math.max(y1, y3) && y2 >= Math.min(y1, y3);
    }

    static final class Validation {
        private final boolean valid;
        private final String message;

        private Validation(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        static Validation valid() {
            return new Validation(true, "");
        }

        static Validation invalid(String message) {
            return new Validation(false, message);
        }

        boolean isValid() {
            return valid;
        }

        String message() {
            return message;
        }
    }
}
