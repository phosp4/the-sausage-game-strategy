package io.github.sausagegame.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Central orchestrator responsible for managing the board state and validating moves.
 */
public final class SausageGame {
    private final GameConfig config;
    private final List<BoardNode> nodes;
    private final List<GameConnection> connections = new ArrayList<>();
    private final TurnManager turnManager;
    private boolean gameOver = false;
    private Player winner;

    public SausageGame(GameConfig config, List<Player> players) {
        this.config = Objects.requireNonNull(config, "config");
        if (players == null || players.size() < 2) {
            throw new IllegalArgumentException("At least two players are required");
        }
        this.nodes = createNodes(config);
        buildAdjacency();
        this.turnManager = new TurnManager(players);
    }

    public List<Player> players() {
        return turnManager.players();
    }

    public Player currentPlayer() {
        return turnManager.currentPlayer();
    }

    public Player nextPlayer() {
        return turnManager.otherPlayer();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player winner() {
        return winner;
    }

    public List<NodeView> getNodeViews() {
        List<NodeView> views = new ArrayList<>(nodes.size());
        for (BoardNode node : nodes) {
            Player occupant = node.occupant();
            views.add(new NodeView(node.id(), node.position().row(), node.position().column(),
                    node.x(), node.y(), node.isOccupied(), occupant));
        }
        return views;
    }

    public List<ConnectionView> getConnections() {
        List<ConnectionView> views = new ArrayList<>(connections.size());
        for (GameConnection connection : connections) {
            views.add(new ConnectionView(connection.fromNodeId(), connection.toNodeId(), connection.owner()));
        }
        return views;
    }

    public Set<Integer> validContinuations(List<Integer> partialMove) {
        if (gameOver) {
            return Set.of();
        }
        return MoveValidator.findContinuations(partialMove, nodes, connections);
    }

    public MoveResult playMove(List<Integer> move) {
        if (gameOver) {
            return new MoveResult(MoveStatus.GAME_OVER, currentPlayer(), nextPlayer(), winner,
                    List.of(), "The game has already finished");
        }
        Player acting = currentPlayer();
        MoveValidator.Validation validation = MoveValidator.validateFullMove(move, nodes, connections);
        if (!validation.isValid()) {
            return new MoveResult(MoveStatus.INVALID, acting, acting, winner,
                    List.of(), validation.message());
        }

        BoardNode first = nodes.get(move.get(0));
        BoardNode second = nodes.get(move.get(1));
        BoardNode third = nodes.get(move.get(2));

        first.occupy(acting);
        second.occupy(acting);
        third.occupy(acting);

        List<ConnectionView> created = new ArrayList<>();
        GameConnection firstConnection = new GameConnection(first.id(), second.id(), acting);
        GameConnection secondConnection = new GameConnection(second.id(), third.id(), acting);
        connections.add(firstConnection);
        connections.add(secondConnection);
        created.add(new ConnectionView(firstConnection.fromNodeId(), firstConnection.toNodeId(), acting));
        created.add(new ConnectionView(secondConnection.fromNodeId(), secondConnection.toNodeId(), acting));

        turnManager.nextTurn();

        if (!MoveValidator.playerHasValidMove(nodes, connections)) {
            gameOver = true;
            winner = acting;
            return new MoveResult(MoveStatus.GAME_OVER, acting, acting, winner,
                    List.copyOf(created), "No further moves available");
        }
        return new MoveResult(MoveStatus.ACCEPTED, acting, currentPlayer(), winner,
                List.copyOf(created), "");
    }

    public void reset() {
        for (BoardNode node : nodes) {
            node.clear();
        }
        connections.clear();
        gameOver = false;
        winner = null;
    }

    private List<BoardNode> createNodes(GameConfig config) {
        List<BoardNode> list = new ArrayList<>();
        int id = 0;
        float spacingX = 1f / (config.columns());
        float spacingY = 1f / (config.rows() + 1f);
        for (int row = 0; row < config.rows(); row++) {
            int columnsInRow = config.columns() - (row % 2);
            for (int col = 0; col < columnsInRow; col++) {
                float offset = (row % 2) * (spacingX / 2f);
                float x = spacingX + col * spacingX + offset;
                float y = 1f - spacingY * (row + 1);
                list.add(new BoardNode(id++, new GridPosition(row, col), x, y));
            }
        }
        return list;
    }

    private void buildAdjacency() {
        Map<GridPosition, BoardNode> byPosition = new HashMap<>();
        for (BoardNode node : nodes) {
            byPosition.put(node.position(), node);
        }
        for (BoardNode node : nodes) {
            int[][] offsets = node.position().row() % 2 == 0 ?
                    new int[][]{{-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {-2, 0}, {2, 0}} :
                    new int[][]{{-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, 0}, {1, 1}, {-2, 0}, {2, 0}};
            List<Integer> neighbors = new ArrayList<>();
            for (int[] offset : offsets) {
                GridPosition neighborPos = node.position().translate(offset[0], offset[1]);
                BoardNode neighbor = byPosition.get(neighborPos);
                if (neighbor != null) {
                    neighbors.add(neighbor.id());
                }
            }
            node.setNeighbors(neighbors);
        }
    }
}
