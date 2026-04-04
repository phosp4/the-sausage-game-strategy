/**
 * vtedy raz to zahralo strategiu zle - hodilo by sa zistit, ze preco
 */

package org.example.strategy;

import lombok.Getter;
import org.example.entities.GameState;
import org.example.entities.Player;
import org.example.entities.Sausage;
import org.example.utils.BitEncoder;

import java.util.HashMap;
import java.util.Map;

public class MinimaxRunner {

    private final Player p1 = new Player("A");
    private final Player p2 = new Player("B");

    private final Map<GameState,Integer> memoVals = new HashMap<>();
    @Getter private final Map<Integer,Long> strategyP1 = new HashMap<>();
    @Getter private final Map<Integer,Long> strategyP2 = new HashMap<>();

    // testing
    @Getter private int counter = 0;

    public int minimaxMemo(GameState gameState, boolean isMaximizingPlayer) {

//        System.out.println("gamestate: " + CliRendererUtil.gridToString(gameState.getGrid()) + ", isMax: " + isMaximizingPlayer);

        // doplnok na behu v threade
        if (Thread.currentThread().isInterrupted()) {
            return -2; // specialna hodnota na oznacenie prerusenia
        }

        if (memoVals.containsKey(gameState)) {
            counter++;
            return memoVals.get(gameState);
        }

        int returnVal;

        if (gameState.isGameOver()) {
            if (isMaximizingPlayer) {
                return -1; // nema tah, teda vyhrava druhy
            } else {
                return 1;
            }
        }

        if (isMaximizingPlayer) {
            int bestValue = -1; // to je ako -infinity
            for (Sausage move : MoveGenerator.getAllPossibleMoves(gameState.getGrid(), p1)) { // pojde to asi aj s O(1) priestorovou
//                System.out.println(Possible);
                gameState.addSausage(move);

                int value = minimaxMemo(gameState, false);

                gameState.removeSausage(move);
                bestValue = Math.max(value, bestValue);
                if (bestValue == 1) {
                    strategyP1.put(gameState.hashCode(), BitEncoder.encodeSausage(move));
                    break;
                }
            }
            returnVal = bestValue;
        }

        else {
            int bestValue = 1; // to je ako +infinity
            for (Sausage move : MoveGenerator.getAllPossibleMoves(gameState.getGrid(), p2)) {
                gameState.addSausage(move);

                int value = minimaxMemo(gameState, true);

                gameState.removeSausage(move);
                bestValue = Math.min(value, bestValue);
                if (bestValue == -1) {
                    strategyP2.put(gameState.hashCode(), BitEncoder.encodeSausage(move));
//                    break; // mozeme *si trufnut* predpokladat, ze super si vyberie tuto cestu; jedina dalsia moznost je 1, ale to nam neuskodi - chceme byt pesimisticky (ale pri hladani konkretnej strategie to uz nemozme urobit)
                }
            }
            returnVal = bestValue;
        }

        memoVals.put(gameState, returnVal);
        return returnVal;
    }

//    /**
//     * returns the optimal strategy for the first player IF EXISTS
//     */
//    public static Map<Integer, Sausage> getFirstPlayerStrategy(int x, int y) {
//        MinimaxRunner sm = new MinimaxRunner();
//        GameState g = new GameState(x, y);
//        int whoIsWinner = sm.minimaxMemo(g, true);
//        Map<Integer, Sausage> firstPlayerStrategy = sm.getStrategyP1();
//        return firstPlayerStrategy;
//    }
}
