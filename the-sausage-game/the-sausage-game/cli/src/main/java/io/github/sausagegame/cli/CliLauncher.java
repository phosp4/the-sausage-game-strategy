package io.github.sausagegame.cli;

import io.github.sausagegame.backend.GameConfig;
import io.github.sausagegame.backend.MoveResult;
import io.github.sausagegame.backend.MoveStatus;
import io.github.sausagegame.backend.Player;
import io.github.sausagegame.backend.SausageGame;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Lightweight command line implementation that shares the same backend as the graphical front end.
 */
public final class CliLauncher {
    private final Scanner scanner;
    private final PrintStream out;
    private final SausageGame game;
    private final Map<Player, String> playerTokens = new LinkedHashMap<>();

    public CliLauncher(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
        Player blue = new Player("Blue Player");
        Player red = new Player("Red Player");
        this.game = new SausageGame(new GameConfig(7, 5), List.of(blue, red));
        playerTokens.put(blue, "B1");
        playerTokens.put(red, "R2");
    }

    public static void main(String[] args) {
        CliLauncher launcher = new CliLauncher(new Scanner(System.in), System.out);
        launcher.run();
    }

    public void run() {
        out.println("Welcome to The Sausage Game (CLI Edition)! Type 'quit' to exit.");
        while (!game.isGameOver()) {
            renderBoard();
            Player current = game.currentPlayer();
            out.printf("%s to move.%n", current.displayName());
            List<Integer> selection = new ArrayList<>(3);
            while (selection.size() < 3) {
                Set<Integer> allowed = game.validContinuations(selection);
                if (allowed.isEmpty()) {
                    out.println("No legal continuation. Type 'back' to undo the previous selection.");
                } else if (!selection.isEmpty()) {
                    out.printf("Available next nodes: %s%n", allowed);
                }
                out.printf("Select node %d: ", selection.size() + 1);
                String line = scanner.nextLine().trim();
                if (line.equalsIgnoreCase("quit")) {
                    out.println("Goodbye!");
                    return;
                }
                if (line.equalsIgnoreCase("back")) {
                    if (!selection.isEmpty()) {
                        selection.remove(selection.size() - 1);
                    }
                    continue;
                }
                try {
                    int id = Integer.parseInt(line);
                    if (!selection.isEmpty() && (allowed.isEmpty() || !allowed.contains(id))) {
                        out.printf("Node %d is not a valid choice.%n", id);
                        continue;
                    }
                    selection.add(id);
                } catch (NumberFormatException ex) {
                    out.println("Please enter a numeric node id, 'back', or 'quit'.");
                }
            }

            MoveResult result = game.playMove(selection);
            if (result.status() == MoveStatus.INVALID) {
                out.printf("Invalid move: %s%n", result.message());
            } else if (result.status() == MoveStatus.ACCEPTED) {
                out.println("Move accepted.\n");
            } else if (result.status() == MoveStatus.GAME_OVER) {
                renderBoard();
                out.printf("%s wins!%n", result.winner().displayName());
                break;
            }
        }
        if (game.isGameOver() && game.winner() != null) {
            out.printf("Congratulations to %s!%n", game.winner().displayName());
        }
    }

    private void renderBoard() {
        out.println();
        List<io.github.sausagegame.backend.NodeView> nodes = game.getNodeViews();
        int maxRow = nodes.stream().mapToInt(io.github.sausagegame.backend.NodeView::row).max().orElse(0);
        for (int row = 0; row <= maxRow; row++) {
            if (row % 2 == 1) {
                out.print("   ");
            }
            nodes.stream()
                    .filter(node -> node.row() == row)
                    .sorted((a, b) -> Integer.compare(a.column(), b.column()))
                    .forEach(node -> {
                        String token = node.occupied()
                                ? playerTokens.getOrDefault(node.occupant(), "@@")
                                : String.format("%02d", node.id());
                        out.print(token + "   ");
                    });
            out.println();
        }
        out.println();
    }
}
