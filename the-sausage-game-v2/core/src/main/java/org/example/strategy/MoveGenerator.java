/**
 * hlavny algoritmus sa da prerobit cez getNeighbours, zatial nechavam tak, nech nebabrem do vsetkeho
 * chat odporucal robit to dokonca cez primitives, ze ani nevytvarat body
 */

package org.example.strategy;

import com.badlogic.gdx.Game;
import org.example.entities.GameBoard;
import org.example.entities.Player;
import org.example.entities.Point;
import org.example.entities.Sausage;
import org.example.utils.ValidatorUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.util.*;

public class MoveGenerator {

    // pozor - toto teraz nie je thread safe
    private static Point p1 = new Point(0,0);
    private static Point p2 = new Point(0,0);
    private static Point p3 = new Point(0,0);
//    private static Point p4 = new Point(0,0);
//    private static Point p5 = new Point(0,0);

    // mozno nie static
    private static Set<Sausage> allPossibleMoves = new HashSet<>();
    private static Set<Sausage> currentlyPossibleMoves = new HashSet<>();

    public static Set<Sausage> updateMovesAfterAdding(Sausage newSausage) {
        currentlyPossibleMoves.clear();

        for (Sausage s : allPossibleMoves) {
            // ak sa neintersectuju (teda neprelinaju a nekrizuju), pridaj ju do currentlyPossibleMoves
        }
        return currentlyPossibleMoves;
    }

    public static Set<Sausage> getPossibleMoves(Sausage[][] grid, Player player) {
        int[][] vectors = {
            {0,-2}, {1,-1}, {2,0}, {1,1}, {0,2}, {-1,1}, {-2,0}, {-1,-1}
        };
        // horizontal / vertical
//        int[][] vectorsHV = {{0,-2}, {2,0}, {0,2}, {-2,0}};

        Set<Sausage> validMoves = new HashSet<>();

        // iteruj gridom, pre kazdy point skusaj
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                p1.setLocation(j, i);
                if (!ValidatorUtil.isPointValidForGrid(p1, grid)) {
                    continue;
                };

                for (int[] v1 : vectors) {
                    p2.setLocation(j + v1[0], i + v1[1]);
                    if (!ValidatorUtil.isPointValidForGrid(p2, grid)) {
                        continue;
                    };
                    if (!ValidatorUtil.haveNoIntersectionInGrid(p1, p2, grid)) {
                        continue;
                    };

//                    for (int k = 0; k < vectorsHV.length; k++) {
//                        if (vectorsHV[k][0] == )
//                    }

                    for (int[] v2 : vectors) {
                        p3.setLocation(j + v1[0] + v2[0], i + v1[1] + v2[1]); // p3 is relative to p2, not p1!
                        if (!ValidatorUtil.isPointValidForGrid(p3, grid)) {
                            continue;
                        };
                        if (!ValidatorUtil.haveNoIntersectionInGrid(p2, p3, grid)) {
                            continue;
                        };

                        Sausage sausage = new Sausage(player, p1.clone(),p2.clone(),p3.clone()); // nove referencie
                        if (ValidatorUtil.isSausageValid(sausage)) {
                            validMoves.add(sausage);
                        }
                    }
                }
            }
        }
        return validMoves;
    }

    /**
     * docasne, len na tu animaciu - aby to islo postupne...
     */
    public static List<Sausage> getPossibleMovesList(Sausage[][] grid, Player player) {
        int[][] vectors = {
            {0,-2}, {1,-1}, {2,0}, {1,1}, {0,2}, {-1,1}, {-2,0}, {-1,-1}
        };
        // horizontal / vertical
//        int[][] vectorsHV = {{0,-2}, {2,0}, {0,2}, {-2,0}};

        List<Sausage> validMoves = new ArrayList<>();

        // iteruj gridom, pre kazdy point skusaj
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                p1.setLocation(j, i);
                if (!ValidatorUtil.isPointValidForGrid(p1, grid)) {
                    continue;
                };

                for (int[] v1 : vectors) {
                    p2.setLocation(j + v1[0], i + v1[1]);
                    if (!ValidatorUtil.isPointValidForGrid(p2, grid)) {
                        continue;
                    };
                    if (!ValidatorUtil.haveNoIntersectionInGrid(p1, p2, grid)) {
                        continue;
                    };

//                    for (int k = 0; k < vectorsHV.length; k++) {
//                        if (vectorsHV[k][0] == )
//                    }

                    for (int[] v2 : vectors) {
                        p3.setLocation(j + v1[0] + v2[0], i + v1[1] + v2[1]); // p3 is relative to p2, not p1!
                        if (!ValidatorUtil.isPointValidForGrid(p3, grid)) {
                            continue;
                        };
                        if (!ValidatorUtil.haveNoIntersectionInGrid(p2, p3, grid)) {
                            continue;
                        };

                        Sausage sausage = new Sausage(player, p1.clone(),p2.clone(),p3.clone()); // nove referencie
                        if (ValidatorUtil.isSausageValid(sausage)) {
                            validMoves.add(sausage);
                        }
                    }
                }
            }
        }
        return validMoves;
    }


//    private static final Logger log = LoggerFactory.getLogger(MoveGenerator.class);

    public static Set<Sausage> getPossibleMoves(Sausage[][] grid) {
        return getPossibleMoves(grid, new Player("tester"));
    }
    // tu sa da pouzit aj getNeighbours a bude to krajsie

    public static void moveGeneratorTester(int x, int y, int maxDepth) {
        GameBoard g = new GameBoard(x, y);

        for (int d = 1; d <= maxDepth; d++) {
            long nodes = countNodes(g, d);
            System.out.println("Level " + (d - 1) + ": " + nodes);
//            Set<Sausage> nodes = countNodesUnique(g, d);
//            System.out.println("Level " + (d - 1) + ": " + nodes.size());
//            CliRendererUtil.printAllPossibleMoves(g, new ArrayList<>(nodes));
        }
    }

    /**
     * toto ma mozno nejaku chybu, treba skontrolovat ak chcem pouzivat
     */
    private static Set<Sausage> countNodesUnique(GameBoard g, int depth) {
        // Get all legal moves for the current state
        Set<Sausage> nodes = getPossibleMoves(g.getGrid());

        // Base Case: If we are at the target leaf depth, return the number of moves found
        if (depth == 1) {
            return nodes;
        }

        Set<Sausage> allChildNodes = new HashSet<>();

        for (Sausage s : nodes) {
            g.addSausage(s);             // Make the move
            allChildNodes.addAll(countNodesUnique(g, depth - 1)); // Recurse
            g.removeSausage(s);       // Un-make the move (backtrack)
        }

        return allChildNodes;
    }

    private static long countNodes(GameBoard g, int depth) {
        // Get all legal moves for the current state
        List<Sausage> moves = new ArrayList<>(getPossibleMoves(g.getGrid()));

        // Base Case: If we are at the target leaf depth, return the number of moves found
        if (depth == 1) {
            return moves.size();
        }

        long totalNodes = 0;

        for (Sausage s : moves) {
            g.addSausage(s);             // Make the move
            totalNodes += countNodes(g, depth - 1); // Recurse
            g.removeSausage(s);       // Un-make the move (backtrack)
        }

        return totalNodes;
    }
}
